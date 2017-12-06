package com.dataengi.crm.contacts.daos

import cats.instances.all._
import com.dataengi.crm.contacts.daos.errors.ContactsBooksDAOErrors
import com.dataengi.crm.contacts.models.ContactsBook
import com.dataengi.crm.contacts.slick.tables.ContactsBookTableDescription
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.daos.AutoIncBaseDAO
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait ContactsBooksDAO extends AutoIncBaseDAO[ContactsBook] {

  def getByOwnerId(ownerId: Long): Or[ContactsBook]

}

@Singleton
class ContactsBooksSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                                    implicit val executionContext: ExecutionContext)
    extends ContactsBooksDAO
    with ContactsBookTableDescription
    with ContactsBookQueries {

  override def get(key: Long): Or[ContactsBook] =
    db.run(selectContactsBookAction(key)).toOrWithLeft(ContactsBooksDAOErrors.ContactsBookNotFound)

  override def getOption(key: Long): Or[Option[ContactsBook]] = db.run(selectContactsBookAction(key)).toOr

  override def add(obj: ContactsBook): Or[Long] = db.run(insertContactsBookAction(obj)).toOr

  override def add(values: List[ContactsBook]): Or[List[Long]] =
    db.run { insertContactsBooksAction(values).map(_.toList) }.toOr

  override def update(obj: ContactsBook): EmptyOr = obj.id match {
    case Some(id) => db.run(updateContactsBookAction(id, obj)).toEmptyOr
    case None     => ContactsBooksDAOErrors.ContactsBookIdIsEmpty.toEmptyOr
  }

  override def delete(key: Long): EmptyOr = db.run(deleteContactsBookAction(key)).toEmptyOr

  override def all: Or[List[ContactsBook]] = db.run(selectAllContactsBooksAction).toOr.map(_.toList)

  override def getByOwnerId(ownerId: Long): Or[ContactsBook] =
    db.run(selectContactsBookByOwnerIdAction(ownerId)).toOrWithLeft(ContactsBooksDAOErrors.ContactsBookNotFound)
}

trait ContactsBookQueries extends ContactsBookTableDescription {

  import profile.api._

  val insertContactsBookQuery = ContactsBooks returning ContactsBooks.map(_.id)

  def insertContactsBookAction(contactsBook: ContactsBook) = insertContactsBookQuery += unMapContactsBook(contactsBook)

  def insertContactsBooksAction(contactsBooks: List[ContactsBook]) =
    insertContactsBookQuery ++= contactsBooks.map(unMapContactsBook)

  def updateContactsBookAction(id: Long, contactsBook: ContactsBook) =
    ContactsBooks.filter(_.id === id).update(ContactsBookRow(id, contactsBook.ownerId, contactsBook.createDate))

  def deleteContactsBookAction(id: Long) = ContactsBooks.filter(_.id === id).delete

  def selectContactsBookAction(id: Long) = ContactsBooks.filter(_.id === id).result.headOption.map(_.map(mapContactsBook))

  def selectContactsBookByOwnerIdAction(ownerId: Long) =
    ContactsBooks.filter(_.ownerId === ownerId).result.headOption.map(_.map(mapContactsBook))

  def selectAllContactsBooksAction = ContactsBooks.result.map(_.map(mapContactsBook))

  val mapContactsBook: (ContactsBookRow) => ContactsBook = row => ContactsBook(row.ownerId, row.createDate, Some(row.id))

  val unMapContactsBook: (ContactsBook) => ContactsBookRow = contactsBook =>
    ContactsBookRow(contactsBook.id.getOrElse(0), contactsBook.ownerId, contactsBook.createDate)

}
