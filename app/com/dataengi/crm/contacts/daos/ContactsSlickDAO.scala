package com.dataengi.crm.contacts.daos

import com.dataengi.crm.contacts.daos.errors.ContactsDAOErrors
import com.dataengi.crm.contacts.models.{Address, Contact, Email, Phone}
import com.dataengi.crm.contacts.slick.tables._
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.daos.AutoIncBaseDAO
import com.dataengi.crm.contacts.models.{Contact, Email, Phone}
import com.dataengi.crm.contacts.slick.tables._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait ContactsSlickDAO extends AutoIncBaseDAO[Contact] {

  def detachGroupFromAllContacts(groupId: Long): EmptyOr

  def detachContactFromGroup(contactId: Long, groupId: Long): EmptyOr

  def findByContactsBookId(contactsBookId: Long): Or[List[Contact]]

  def attachContactToGroup(contactId: Long, groupId: Long): EmptyOr

  def get(contactIds: List[Long]): Or[List[Contact]]

  def findByGroupId(groupId: Long): Or[List[Contact]]

  def addAndReturnContact(contact: Contact): Or[Contact]

  def updateAdvertiserId(id: Long, advertiserId: Option[Long]): EmptyOr
}

@Singleton
class ContactsSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                               implicit val executionContext: ExecutionContext)
    extends ContactsSlickDAO
    with ContactsTableDescription
    with GroupToContactsTableDescription
    with ContactsQueries {

  override def get(id: Long): Or[Contact] =
    db.run(selectContact(id)).toOrWithLeft(ContactsDAOErrors.contactWithIdNotFound(id))

  override def getOption(id: Long): Or[Option[Contact]] = db.run(selectContactOption(id)).toOr

  override def all: Or[List[Contact]] = db.run(selectAllContacts).toOr

  override def update(contact: Contact): EmptyOr = contact.id match {
    case Some(id) => db.run(updateContact(id, contact)).toEmptyOr
    case None     => ContactsDAOErrors.ContactIdIsEmpty.toErrorOrWithType[Empty]
  }

  override def add(contact: Contact): Or[Long] = db.run(insertContact(contact)).toOr

  override def addAndReturnContact(contact: Contact): Or[Contact] = db.run(insertAndReturnContact(contact)).toOr

  override def add(contacts: List[Contact]): Or[List[Long]] = db.run(insertContacts(contacts)).toOr

  override def delete(id: Long): EmptyOr = db.run(deleteContact(id)).toEmptyOr

  override def detachContactFromGroup(contactId: Long, groupId: Long): EmptyOr =
    db.run(removeContactToGroupLink(contactId, groupId)).toEmptyOr

  override def findByContactsBookId(contactsBookId: Long): Or[List[Contact]] =
    db.run(selectContactsByContactsBook(contactsBookId)).toOr

  override def attachContactToGroup(contactId: Long, groupId: Long): EmptyOr =
    db.run(insertContactToGroupLink(groupId, contactId)).toEmptyOr

  override def get(contactIds: List[Long]): Or[List[Contact]] = db.run(selectContactsByIds(contactIds)).toOr

  override def findByGroupId(groupId: Long): Or[List[Contact]] = db.run(selectContactsByGroup(groupId)).toOr

  override def detachGroupFromAllContacts(groupId: Long): EmptyOr = db.run(removeGroupFromAllContacts(groupId)).toEmptyOr

  override def updateAdvertiserId(id: Long, advertiserId: Option[Long]): EmptyOr =
    db.run(updateAdvertiserIdAction(id, advertiserId)).toEmptyOr
}

trait ContactsQueries
    extends EmailTableDescription
    with PhoneTableDescription
    with GroupTableDescription
    with GroupToContactsTableDescription
    with AddressTableDescription {

  implicit val executionContext: ExecutionContext

  import profile.api._

  def selectContactRow(id: Long) = Contacts.filter(_.id === id).result.headOption.flatMap {
    case None        => DBIO.failed(new RuntimeException("Contact is not exist"))
    case Some(value) => DBIO.successful(value)
  }

  def selectContactRowOption(id: Long) = Contacts.filter(_.id === id).result.headOption

  def selectContact(contactId: Long) =
    for {
      contactRow <- selectContactRow(contactId)
      emails     <- selectEmails(contactId)
      groupIds   <- selectGroupIds(contactRow.id)
      phones     <- selectPhones(contactId)
      address    <- selectAddress(contactRow.addressId.getOrElse(-1))
    } yield {
      mapContact(contactRow, emails, groupIds, phones, address)
    }

  def selectContactBaseInfo(id: Long) =
    Contacts.filter(_.id === id).map(contactRow => (contactRow.name, contactRow.company)).result.headOption.flatMap {
      case None        => DBIO.failed(new RuntimeException("Contact is not exist"))
      case Some(value) => DBIO.successful(value)
    }

  def selectContactOption(contactId: Long) =
    selectContactRowOption(contactId).flatMap {
      case Some(contactRow) => contactRowToContact(contactRow).map(Some(_))
      case None             => DBIO.successful[Option[Contact]](None)
    }

  def selectAllContacts = Contacts.result.flatMap(contactRows => DBIO.sequence(contactRows.map(contactRowToContact).toList))

  def selectContactsByIds(contactIds: List[Long]) =
    Contacts
      .filter(_.id inSetBind contactIds)
      .result
      .flatMap(contactRows => DBIO.sequence(contactRows.map(contactRowToContact).toList))

  def selectContactsByGroup(groupId: Long) =
    (GroupToContacts filter (_.groupId === groupId) join Contacts on (_.contactId === _.id)).result
      .flatMap(joinedResultToContacts)

  def selectContactsByContactsBook(contactsBookId: Long) =
    Contacts
      .filter(_.contactsBookId === contactsBookId)
      .result
      .flatMap(contactRows => DBIO.sequence(contactRows.map(contactRowToContact).toList))

  def insertContact(contact: Contact) =
    (for {
      possibleAddressId <- insertAddress(contact)
      contactForInserting = possibleAddressId match {
        case -1L => contact
        case _   => contact.copy(address = contact.address.map(adr => adr.copy(id = Some(possibleAddressId))))
      }
      contactId <- Contacts returning Contacts.map(_.id) += unMapContact(contactForInserting)
      newContact = contact.copy(id = Some(contactId))
      insertGroups <- insertContactToGroupsLink(contactId, newContact.groupIds)
      insertEmails <- insertEmails(newContact)
      insertPhones <- insertPhones(newContact)
    } yield contactId).transactionally

  def insertAndReturnContact(contact: Contact) = {
    val addresstoInsert = contact.address.getOrElse(Address(None, None, None, None, None, None))
    (for {
      possibleAddressId <- Addresses returning Addresses.map(_.id) += unmapAddress(addresstoInsert)
      contactForInserting = contact.copy(address = Some(addresstoInsert.copy(id = Some(possibleAddressId))))
      contactId <- Contacts returning Contacts.map(_.id) += unMapContact(contactForInserting)
      newContact = contactForInserting.copy(id = Some(contactId))
      insertGroups <- insertContactToGroupsLink(contactId, newContact.groupIds)
      insertEmails <- insertEmails(newContact)
      insertPhones <- insertPhones(newContact)
    } yield newContact).transactionally
  }

  def insertContacts(contacts: List[Contact]) = {
    DBIO.sequence(contacts.map(insertContact))
  }

  def updateContact(id: Long, contact: Contact) =
    (for {
      updateEmails   <- updateEmails(contact)
      updatePhones   <- updatePhones(contact)
      updateGroupIds <- updateContactToGroupsLink(id, contact.groupIds)
      updateAddress <- {
        updateAddress(contact, contact.address.headOption.flatMap(_.id))
      }
      updateContact <- {
        Contacts.filter(_.id === id).update(unMapContact(contact))
      }
    } yield updateContact).transactionally

  def updateAdvertiserIdAction(id: Long, advertiserId: Option[Long]) =
    Contacts.filter(_.id === id).map(_.advertiserId).update(advertiserId)

  def updateContactOLD(id: Long, contact: Contact) =
    (for {
      updateEmails   <- updateEmails(contact)
      updatePhones   <- updatePhones(contact)
      updateGroupIds <- updateContactToGroupsLink(id, contact.groupIds)
      updateAddress  <- updateAddresIfNotEmpty(contact)
      updateContact <- {
        val unmappedContact = unMapContact(contact)
        val contactRowToUpdate = updateAddress match {
          case None =>
            if (contact.address.isEmpty)
              unmappedContact
            else
              unmappedContact.copy(addressId = None)
          case Some(addressId) =>
            if (contact.address.isEmpty)
              unmappedContact.copy(addressId = Some(addressId))
            else
              unmappedContact
        }
        Contacts.filter(_.id === id).update(contactRowToUpdate)
      }
    } yield updateContact).transactionally

  def deleteContact(id: Long) =
    (for {
      deleteContact <- Contacts.filter(_.id === id).delete
      removeEmails  <- removeEmails(id)
      removePhones  <- removePhones(id)
      removeAddress <- removeAddress(id)
    } yield deleteContact).transactionally

  def selectEmails(contactId: Long) = Emails.filter(_.contactId === contactId).result.map(_.map(mapEmail))

  def insertEmails(contact: Contact) =
    Emails returning Emails.map(_.id) ++= contact.emails.map(unMapEmail(_, contact.id.getOrElse(0L)))

  def removeEmails(contact: Contact) = Emails.filter(_.id inSetBind contact.emails.flatMap(_.id)).delete

  def removeEmails(contactId: Long) = Emails.filter(_.contactId === contactId).delete

  def updateEmails(contact: Contact) =
    (for {
      removeEmails <- removeEmails(contact.id.get)
      insertEmails <- insertEmails(contact)
    } yield insertEmails).transactionally

  def selectPhones(contactId: Long) = Phones.filter(_.contactId === contactId).result.map(_.map(mapPhone))

  def insertPhones(contact: Contact) =
    Phones returning Phones.map(_.id) ++= contact.phones.map(unMapPhone(_, contact.id.getOrElse(0L)))

  def removePhones(contact: Contact) = Phones.filter(_.id inSetBind contact.phones.flatMap(_.id)).delete

  def removePhones(contactId: Long) = Phones.filter(_.contactId === contactId).delete

  def updatePhones(contact: Contact) =
    (for {
      removePhones <- removePhones(contact.id.get)
      insertPhones <- insertPhones(contact)
    } yield insertPhones).transactionally

  def selectGroupIds(contactId: Long) = GroupToContacts.filter(_.contactId === contactId).result.map(_.map(_.groupId))

  def selectAddress(addressId: Long) = Addresses.filter(_.id === addressId).result.headOption.map(_.map(mapAddress))

  def insertAddress(contact: Contact) = contact.address match {
    case Some(address) => Addresses returning Addresses.map(_.id) += unmapAddress(address)
    case None          => DBIO.successful(-1L) //TODO: Change this to Option
  }

  def updateAddress(contact: Contact, addressId: Option[Long]) = contact.address match {
    case Some(address) => Addresses.filter(_.id === addressId.getOrElse(0L)).update(unmapAddress(address))
    case None          => removeAddress(contact.id.getOrElse(0L))
  }

  def updateAddresIfNotEmpty(contact: Contact) = {
    contact.address match {
      case None => {
        removeAddress(contact.id.getOrElse(0L)).map(_ => None)
      }
      case Some(address) => {
        address.id match {
          case Some(addressId) if address.isEmpty =>
            removeAddress(contact.id.getOrElse(0L)).map(_ => None)
          case Some(addressId) if address.nonEmpty =>
            Addresses.filter(_.id === addressId).update(unmapAddress(address)).map(_ => Some(addressId))
          case None if address.isEmpty =>
            DBIO.successful(None)
          case None if address.nonEmpty =>
            (Addresses returning Addresses.map(_.id) += unmapAddress(address)).map(Some(_))
        }
      }
    }
  }

  def removeAddress(addressId: Long) = Addresses.filter(_.id === addressId).delete

  def contactRowToContact(contactRow: ContactRow) =
    for {
      emails   <- selectEmails(contactRow.id)
      groupIds <- selectGroupIds(contactRow.id)
      phones   <- selectPhones(contactRow.id)
      address  <- selectAddress(contactRow.addressId.getOrElse(-1))
    } yield {
      mapContact(contactRow, emails, groupIds, phones, address)
    }

  def mapContact(contact: ContactRow,
                 emails: Seq[Email],
                 groupIds: Seq[Long],
                 phones: Seq[Phone],
                 address: Option[Address]) = {
    Contact(
      contact.name,
      contact.contactsBookId,
      contact.createDate,
      emails.toList,
      groupIds.toList,
      phones.toList,
      contact.skypeId,
      contact.fax,
      contact.company,
      contact.jobPosition,
      address,
      contact.timeZone,
      contact.language,
      contact.contactType,
      contact.note,
      Some(contact.id),
      contact.advertiserId
    )
  }

  def unMapContact(contact: Contact) = ContactRow(
    contact.id.getOrElse(0L),
    contact.name,
    contact.contactsBookId,
    contact.createDate,
    contact.skypeId,
    contact.fax,
    contact.company,
    contact.jobPosition,
    contact.address.map(_.id.getOrElse(0L)),
    contact.timeZone,
    contact.language,
    contact.contactType,
    contact.note,
    contact.advertiserId
  )

  def mapEmail(emailRow: EmailRow): Email = Email(emailRow.emailType, emailRow.email, Some(emailRow.id))

  def unMapEmail(email: Email, contactId: Long): EmailRow =
    EmailRow(email.id.getOrElse(0L), email.emailType, email.email, contactId)

  def mapPhone(phoneRow: PhoneRow): Phone = Phone(phoneRow.phoneType, phoneRow.phone, Some(phoneRow.id))

  def unMapPhone(phone: Phone, contactId: Long): PhoneRow =
    PhoneRow(phone.id.getOrElse(0L), phone.phoneType, phone.phone, contactId)

  def mapAddress(addressRow: AddressRow): Address =
    Address(addressRow.street,
            addressRow.state,
            addressRow.country,
            addressRow.city,
            addressRow.zipCode,
            Some(addressRow.id))

  def unmapAddress(address: Address): AddressRow =
    AddressRow(address.id.getOrElse(0L), address.street, address.state, address.country, address.city, address.zipCode)

  def joinedResultToContacts(joinedResult: Seq[(GroupToContactRow, ContactRow)]) = {
    DBIO
      .sequence(joinedResult.groupBy { case (groupToContact, contact) => contact }.map {
        case (contact, seq) =>
          DBIO.sequence(seq.map {
            case (groupToContact, contactRow) =>
              contactRowToContact(contactRow)
          })
      })
      .map(rs => rs.flatten.toList)
  }

  def insertContactToGroupLink(groupId: Long, contactId: Long) = GroupToContacts += GroupToContactRow(groupId, contactId)

  def insertContactToGroupsLink(contactId: Long, groupIds: List[Long]) =
    GroupToContacts ++= groupIds.map(GroupToContactRow(_, contactId))

  def updateContactToGroupsLink(contactId: Long, groupIds: List[Long]) =
    for {
      removeGroupIds <- removeContactToGroupsLink(contactId)
      insertGroupIds <- insertContactToGroupsLink(contactId, groupIds)
    } yield {
      insertGroupIds
    }

  def removeContactToGroupLink(contactId: Long, groupId: Long) =
    GroupToContacts.filter(row => row.groupId === groupId && row.contactId === contactId).delete

  def removeContactToGroupsLink(contactId: Long) =
    GroupToContacts.filter(groupToContact => groupToContact.contactId === contactId).delete

  def removeGroupFromAllContacts(groupId: Long) = GroupToContacts.filter(_.groupId === groupId).delete

}
