package com.dataengi.crm.contacts.services

import cats.instances.all._
import com.dataengi.crm.contacts.controllers.data._
import com.dataengi.crm.contacts.models.{Address, Contact, Email, Phone}
import com.dataengi.crm.contacts.repositories.ContactsRepository
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import org.joda.time.DateTime
import cats.syntax.traverse._

import scala.concurrent.ExecutionContext

trait ContactsService {

  def getGroupMembers(groupId: Long): Or[List[Contact]]

  def removeContactFromGroup(data: LeaveGroupData): EmptyOr

  def removeContacts(data: RemoveContactsData): EmptyOr

  def addGroup(data: AddContactsToGroupData): EmptyOr

  def createOLD(contact: CreateContactData, userId: Long): Or[Contact]

  def create(contact: CreateContactData, userId: Long): Or[Contact]

  def findAllFromContactsBook(contactsBookId: Long): Or[List[Contact]]

  def update(updateContactData: UpdateContactData, userId: Long): Or[Contact]

  def remove(id: Long): EmptyOr

  def getOption(id: Long): Or[Option[Contact]]

  def get(id: Long): Or[Contact]

  def getContacts(offset: Int = 0, limit: Int = 20): Or[List[Contact]]

  def getUserContacts(userId: Long): Or[List[Contact]]

  def updateAdvertiserId(id: Long, advertiserId: Long): EmptyOr

  def deleteAdvertiserId(id: Long): EmptyOr
}

@Singleton
class ContactsServiceImplementation @Inject()(contactsBookService: ContactsBooksService,
                                              contactsRepository: ContactsRepository,
                                              implicit val executionContext: ExecutionContext)
    extends ContactsService {

  override def createOLD(contactData: CreateContactData, userId: Long): Or[Contact] =
    for {
      contactBook <- contactsBookService.findByOwner(userId)
      contact = ContactsService.mapCreateContactData(contactData, contactBook.id.get)
      contactId <- contactsRepository.add(contact)
    } yield contact.copy(id = Some(contactId))

  override def create(contactData: CreateContactData, userId: Long): Or[Contact] =
    for {
      contactBook <- contactsBookService.findByOwner(userId)
      contact = ContactsService.mapCreateContactData(contactData, contactBook.id.get)
      newContact <- contactsRepository.insertAndReturnContactWithAddress(contact)
    } yield newContact

  override def update(updateContactData: UpdateContactData, userId: Long): Or[Contact] =
    for {
      contactsBook <- contactsBookService.get(updateContactData.contactsBookId)
      contact = ContactsService.mapUpdateContactData(updateContactData)
      updateResult <- contactsRepository.update(updateContactData.id, contact)
    } yield contact

  override def remove(id: Long): EmptyOr = contactsRepository.remove(id)

  override def getOption(id: Long): Or[Option[Contact]] = contactsRepository.getOption(id)

  override def get(id: Long): Or[Contact] = contactsRepository.get(id)

  override def getContacts(offset: Int, limit: Int): Or[List[Contact]] = contactsRepository.getAll()

  override def getUserContacts(userId: Long): Or[List[Contact]] = {
    for {contactsBook <- contactsBookService.findByOwner(userId)
         contacts <- contactsRepository.getByContactsBookId(contactsBook.id.get)
    } yield contacts
  }

  override def findAllFromContactsBook(contactsBookId: Long): Or[List[Contact]] =
    contactsRepository.getByContactsBookId(contactsBookId)

  override def addGroup(data: AddContactsToGroupData): EmptyOr =
    for {
      updateContact <- data.contactIds.traverse(addGroup(_, data.groupId)).toEmptyOr
    } yield updateContact

  private def addGroup(contactId: Long, groupId: Long): EmptyOr =
    for {
      contact           <- get(contactId)
      updateGroupResult <- contactsRepository.attachContactToGroup(contact, groupId)
    } yield updateGroupResult

  override def removeContacts(data: RemoveContactsData): EmptyOr =
    data.contactIds.traverse(contactsRepository.remove).toEmptyOr

  override def removeContactFromGroup(data: LeaveGroupData): EmptyOr =
    for {
      contacts    <- contactsRepository.get(data.contactIds)
      removeGroup <- contacts.map(contactsRepository.detachContactFromGroup(_, data.groupId)).foldableSkipLeft.toEmptyOr
    } yield removeGroup

  override def getGroupMembers(groupId: Long): Or[List[Contact]] = contactsRepository.findContactsByGroupId(groupId)

  override def updateAdvertiserId(id: Long, advertiserId: Long): EmptyOr = contactsRepository.updateAdvertiserId(id, Some(advertiserId))

  override def deleteAdvertiserId(id: Long): EmptyOr = contactsRepository.updateAdvertiserId(id, None)
}

object ContactsService {

  def mapUpdateContactData(contactData: UpdateContactData): Contact = {
    Contact(
      name = contactData.name,
      contactsBookId = contactData.contactsBookId,
      createDate = DateTime.now().getMillis, //TODO: Changing create date after update???
      groupIds = contactData.groupIds,
      emails = contactData.emails.map(data => Email(data.emailType, data.email)),
      phones = contactData.phones.map(data => Phone(data.phoneType, data.phone)),
      skypeId = contactData.skypeId,
      address = contactData.address.map(data => Address(data.street, data.state, data.country, data.city, data.zipCode, data.id)),
      fax = contactData.fax,
      company = contactData.company,
      jobPosition = contactData.jobPosition,
      note = contactData.note,
      timeZone = contactData.timeZone,
      language = contactData.language,
      contactType = contactData.contactType,
      id = Some(contactData.id),
      advertiserId = contactData.advertiserId
    )
  }

  def mapCreateContactData(contactData: CreateContactData, contactsBookId: Long): Contact = {
    Contact(
      name = contactData.name,
      contactsBookId = contactsBookId,
      createDate = DateTime.now().getMillis,
      emails = contactData.emails.map(data => Email(data.emailType, data.email)),
      phones = contactData.phones.map(data => Phone(data.phoneType, data.phone)),
      skypeId = contactData.skypeId,
      address = contactData.address.map(data => Address(data.street, data.state, data.country, data.city, data.zipCode)),
      fax = contactData.fax,
      company = contactData.company,
      jobPosition = contactData.jobPosition,
      timeZone = contactData.timeZone,
      language = contactData.language,
      contactType = contactData.contactType,
      note = contactData.note
    )
  }

}
