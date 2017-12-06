package com.dataengi.crm.contacts.repositories

import cats.instances.all._
import com.dataengi.crm.contacts.daos.ContactsBooksDAO
import com.dataengi.crm.contacts.models.ContactsBook
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}

import scala.concurrent.ExecutionContext

trait ContactsBooksRepository extends AutoIncRepository[ContactsBook] {

  def findByOwner(userId: Long): Or[Option[ContactsBook]]

}

@Singleton
class ContactsBooksInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[ContactsBook]
    with ContactsBooksRepository {

  override def beforeSave(key: Long, value: ContactsBook): ContactsBook = value.copy(id = Some(key))

  override def findByOwner(userId: Long): Or[Option[ContactsBook]] = getAll().map(_.find(findByOwnerId(_, userId)))

  private def findByOwnerId(book: ContactsBook, userId: Long): Boolean = book.ownerId == userId

}

@Singleton
class ContactsBooksRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext,
                                                      contactsBooksDAO: ContactsBooksDAO)
    extends ContactsBooksRepository {

  override def findByOwner(userId: Long): Or[Option[ContactsBook]] =
    contactsBooksDAO.getByOwnerId(userId).map(Option(_)).recoverWithDefault(None)

  override def getAll(): Or[List[ContactsBook]] = contactsBooksDAO.all

  override def remove(id: Long): Or[Empty] = contactsBooksDAO.delete(id)

  override def add(value: ContactsBook): Or[Long] = contactsBooksDAO.add(value)

  override def add(values: List[ContactsBook]): Or[List[Long]] = contactsBooksDAO.add(values)

  override def get(id: Long): Or[ContactsBook] = contactsBooksDAO.get(id)

  override def update(id: Long, value: ContactsBook): EmptyOr = contactsBooksDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[ContactsBook]] = contactsBooksDAO.getOption(id)

}
