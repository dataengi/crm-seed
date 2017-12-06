package com.dataengi.crm.contacts.services

import cats.instances.all._
import com.dataengi.crm.contacts.models.ContactsBook
import com.dataengi.crm.contacts.repositories.ContactsBooksRepository
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext

trait ContactsBooksService {

  def update(value: ContactsBook): EmptyOr

  def get(id: Long): Or[ContactsBook]

  def findByOwner(userId: Long): Or[ContactsBook]

  def create(userId: Long): Or[ContactsBook]

}

@Singleton
class ContactsBooksServiceImplementation @Inject()(contactsBookRepository: ContactsBooksRepository,
                                                   implicit val executionContext: ExecutionContext)
    extends ContactsBooksService {

  override def findByOwner(userId: Long): Or[ContactsBook] =
    for {
      contactsBookOpt <- contactsBookRepository.findByOwner(userId)
      contactsBook    <- getOrCreate(contactsBookOpt, userId)
    } yield contactsBook

  override def create(userId: Long): Or[ContactsBook] = {
    val contactsBook = ContactsBook(ownerId = userId, createDate = DateTime.now().getMillis)
    contactsBookRepository.add(contactsBook).map(id => contactsBook.copy(id = Some(id)))
  }

  private def getOrCreate(contactBookOption: Option[ContactsBook], userId: Long) = contactBookOption match {
    case Some(contactsBook) => contactsBook.toOr
    case None               => create(userId)
  }

  override def get(id: Long): Or[ContactsBook] =
    for {
      contactsBook <- contactsBookRepository.get(id)
    } yield contactsBook

  override def update(contact: ContactsBook): EmptyOr = contactsBookRepository.update(contact.id.get, contact)

}
