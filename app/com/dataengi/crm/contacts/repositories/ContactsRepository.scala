package com.dataengi.crm.contacts.repositories

import cats.instances.all._
import com.dataengi.crm.contacts.daos.ContactsSlickDAO
import com.dataengi.crm.contacts.models.Contact
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}
import cats.syntax.traverse._

import scala.concurrent.ExecutionContext

trait ContactsRepository extends AutoIncRepository[Contact] {

  def findContactsByGroupId(groupId: Long): Or[List[Contact]]

  def get(contactIds: List[Long]): Or[List[Contact]]

  def attachContactToGroup(contactId: Contact, groupId: Long): EmptyOr

  def getByContactsBookId(contactsBookId: Long): Or[List[Contact]]

  def detachContactFromGroup(contact: Contact, group: Long): EmptyOr

  def detachGroupFromAllContacts(group: Long): EmptyOr

  def insertAndReturnContactWithAddress(contact: Contact): Or[Contact]

  def updateAdvertiserId(id: Long, advertiserId: Option[Long]): EmptyOr
}

@Singleton
class ContactsInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
  extends BaseInMemoryRepository[Contact]
    with ContactsRepository {

  override def beforeSave(key: Long, value: Contact): Contact = value.copy(id = Some(key))

  override def getByContactsBookId(contactsBookId: Long): Or[List[Contact]] =
    getAll().map(_.filter(_.contactsBookId == contactsBookId))

  override def attachContactToGroup(contact: Contact, groupId: Long): EmptyOr =
    update(contact.id.get, contact.copy(groupIds = contact.groupIds :+ groupId))

  override def detachContactFromGroup(contact: Contact, groupId: Long): EmptyOr =
    update(contact.id.get, contact.copy(groupIds = contact.groupIds.filter(_ == groupId)))

  override def detachGroupFromAllContacts(groupId: Long): EmptyOr =
    for {
      contacts <- getAll()
      removeResult <- contacts.traverse(detachContactFromGroup(_, groupId)).toEmptyOr
    } yield removeResult

  override def get(contactIds: List[Long]): Or[List[Contact]] = contactIds.map(get).foldableSkipLeft

  override def findContactsByGroupId(groupId: Long): Or[List[Contact]] =
    getAll().map(_.filter(_.groupIds.contains(groupId)))

  override def insertAndReturnContactWithAddress(contact: Contact): Or[Contact] = {
    add(contact).map(nId => contact.copy(id = Some(nId)))
  }

  override def updateAdvertiserId(id: Long, advertiserId: Option[Long]): EmptyOr =
    for {
      contact <- get(id)
      updateResult <- update(id, contact.copy(advertiserId = advertiserId))
    } yield updateResult
}

@Singleton
class ContactsRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext,
                                                 contactsSlickDAO: ContactsSlickDAO)
  extends ContactsRepository {

  override def findContactsByGroupId(groupId: Long): Or[List[Contact]] = contactsSlickDAO.findByGroupId(groupId)

  override def get(contactIds: List[Long]): Or[List[Contact]] = contactsSlickDAO.get(contactIds)

  override def attachContactToGroup(contact: Contact, groupId: Long): EmptyOr =
    contactsSlickDAO.attachContactToGroup(contact.id.get, groupId)

  override def getByContactsBookId(contactsBookId: Long): Or[List[Contact]] =
    contactsSlickDAO.findByContactsBookId(contactsBookId)

  override def detachContactFromGroup(contact: Contact, group: Long): EmptyOr =
    contactsSlickDAO.detachContactFromGroup(contact.id.get, group)

  override def detachGroupFromAllContacts(group: Long): EmptyOr = contactsSlickDAO.detachGroupFromAllContacts(group)

  override def getAll(): Or[List[Contact]] = contactsSlickDAO.all

  override def remove(id: Long): Or[Empty] = contactsSlickDAO.delete(id)

  override def add(value: Contact): Or[Long] = contactsSlickDAO.add(value)

  override def insertAndReturnContactWithAddress(contact: Contact): Or[Contact] =
    contactsSlickDAO.addAndReturnContact(contact)

  override def add(values: List[Contact]): Or[List[Long]] = contactsSlickDAO.add(values)

  override def get(id: Long): Or[Contact] = contactsSlickDAO.get(id)

  override def update(id: Long, value: Contact): EmptyOr = contactsSlickDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[Contact]] = contactsSlickDAO.getOption(id)

  override def updateAdvertiserId(id: Long, advertiserId: Option[Long]): EmptyOr = contactsSlickDAO.updateAdvertiserId(id, advertiserId)
}
