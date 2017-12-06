package com.dataengi.crm.contacts.daos

import com.dataengi.crm.common.context.CRMApplication
import com.dataengi.crm.contacts.models.ContactsBook
import org.joda.time.DateTime
import play.api.test.PlaySpecification
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.context.types._
import org.specs2.runner.SpecificationsFinder

class ContactsBooksDAOSpec extends PlaySpecification with CRMApplication with SpecificationsFinder {

  sequential

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[ContactsBooksDAO].to[ContactsBooksSlickDAOImplementation]
    )
  }

  lazy val contactsBooksDAO: ContactsBooksDAO = application.injector.instanceOf[ContactsBooksDAO]

  val TestOwnerId = 0L

  lazy val TestContactsBook: ContactsBook = ContactsBook(TestOwnerId, new DateTime().getMillis)

  "ContactsBooksDAO" should {
    "add contacts book and get" in {
      val addContactsBookResult = contactsBooksDAO.add(TestContactsBook).await()
      addContactsBookResult.isRight === true
      val id                    = addContactsBookResult.value
      val getContactsBookResult = contactsBooksDAO.get(id).await()
      getContactsBookResult.isRight === true
      getContactsBookResult.value === TestContactsBook.copy(id = Some(id))
      val getContactsBookOptionResult = contactsBooksDAO.getOption(id).await()
      println(s"[contacts-books-dao][get-option] $getContactsBookOptionResult")
      getContactsBookOptionResult.isRight === true
      getContactsBookOptionResult.value === Some(TestContactsBook.copy(id = Some(id)))
      val getContactsBookByOwnerIdResult = contactsBooksDAO.getByOwnerId(TestOwnerId).await()
      getContactsBookByOwnerIdResult.isRight === true
    }

    "add and delete" in {
      val addContactsBookResult = contactsBooksDAO.add(TestContactsBook).await()
      addContactsBookResult.isRight === true
      val id                    = addContactsBookResult.value
      val getContactsBookResult = contactsBooksDAO.get(id).await()
      getContactsBookResult.isRight === true
      getContactsBookResult.value === TestContactsBook.copy(id = Some(id))
      val deleteContactsBookResult = contactsBooksDAO.delete(id).await()
      deleteContactsBookResult.isRight === true
    }

    "add and update" in {
      val addContactsBookResult = contactsBooksDAO.add(TestContactsBook).await()
      addContactsBookResult.isRight === true
      val id                    = addContactsBookResult.value
      val getContactsBookResult = contactsBooksDAO.get(id).await()
      getContactsBookResult.isRight === true
      getContactsBookResult.value === TestContactsBook.copy(id = Some(id))
      val updatedContactsBook      = TestContactsBook.copy(id = Some(id), createDate = new DateTime().getMillis)
      val updateContactsBookResult = contactsBooksDAO.update(updatedContactsBook).await()
      updateContactsBookResult.isRight === true
      val getUpdatedContactsBookResult = contactsBooksDAO.get(id).await()
      getUpdatedContactsBookResult.isRight === true
      getUpdatedContactsBookResult.value === updatedContactsBook
    }

  }

}
