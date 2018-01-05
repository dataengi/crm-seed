package com.dataengi.crm.contacts.daos.contacts

import com.dataengi.crm.contacts.models._
import org.joda.time.DateTime
import play.api.test.PlaySpecification
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.contacts.daos.context.ContactsDAOContext
import org.specs2.runner.SpecificationsFinder

class ContactsDAOSpec extends PlaySpecification with ContactsDAOContext {

  sequential

  "ContactsDAO".should {
    "add contact and get it" in {

      val contactsBookId = createContactsBook()

      val groupId1 = createGroup(contactsBookId)
      val groupId2 = createGroup(contactsBookId)
      val groupId3 = createGroup(contactsBookId)

      val testContactFull = testContactFullWithoutRealIds.copy(groupIds = List(groupId1, groupId2, groupId3))

      val newContactId   = createContact(testContactFull)
      val newContactFull = getContact(newContactId)

      ignoreIds(newContactFull) === testContactFull.copy(id = Some(newContactId))
      getContactOption(newContactId).map(ignoreIds) === Some(testContactFull.copy(id = Some(newContactId)))

      val testContactNotFull =
        testContactFull.copy(phones = List(Phone(ContactFieldTypes.Other, "102")), emails = Nil)
      val newContactNotFullId = createContact(testContactNotFull)
      val newContactNotFull   = getContact(newContactNotFullId)

      ignoreIds(newContactNotFull) === testContactNotFull.copy(id = Some(newContactNotFullId))

      val allContacts = getAllContacts()

      allContacts.nonEmpty === true
      allContacts.contains(newContactFull) === true
      allContacts.contains(newContactNotFull) === true
    }

    "update contact" in {

      val contactId  = createContact(testContactFullWithoutRealIds)
      val newContact = getContact(contactId)

      val contactsBookId = createContactsBook()

      val groupId1 = createGroup(contactsBookId)
      val groupId2 = createGroup(contactsBookId)

      val contactForUpdating = newContact.copy(
        name = "updatedName",
        emails = newContact.emails :+ Email(ContactFieldTypes.Home, "scala@is.ok"),
        groupIds = List(groupId1, groupId2),
        phones = newContact.phones :+ Phone(ContactFieldTypes.Other, "121212"),
        fax = None,
        address = None,
        contactType = None
      )

      updateContact(contactForUpdating)

      val updatedContact = getContact(contactId)
      ignoreIds(updatedContact) === ignoreIds(contactForUpdating)

    }

    "attach group to contacts" in {
      val contactsBookId = createContactsBook()

      val groupId1 = createGroup(contactsBookId)
      val groupId2 = createGroup(contactsBookId)
      val groupId3 = createGroup(contactsBookId)

      val contactForInsert = testContactFullWithoutRealIds.copy(groupIds = List(groupId1, groupId2, groupId3))

      val contactId = createContact(contactForInsert)

      val newContact = getContact(contactId)

      ignoreIds(newContact) === contactForInsert.copy(id = Some(contactId))

      updateContact(newContact.copy(groupIds = List(groupId1)))

      ignoreIds(getContact(contactId)) === contactForInsert.copy(id = Some(contactId), groupIds = List(groupId1))

    }

  }

  def createContact(contact: Contact): Long = {
    val addContactsResult = contactsDAO.add(contact).await()
    addContactsResult.isRight === true
    addContactsResult.value
  }

  def getContact(id: Long): Contact = {
    val getContactByIdResult = contactsDAO.get(id).await()
    getContactByIdResult.isRight === true
    getContactByIdResult.value
  }

  def getContactOption(id: Long): Option[Contact] = {
    val getContactOptionResult = contactsDAO.getOption(id).await()
    getContactOptionResult.isRight === true
    getContactOptionResult.value
  }

  def getAllContacts(): List[Contact] = {
    val getAllContactsResult = contactsDAO.all.await()
    getAllContactsResult.isRight === true
    getAllContactsResult.value
  }

  def createGroup(contactsBookId: Long): Long = {
    val addGroupResult = groupsDAO.add(Group("TEST_GROUP", contactsBookId, 0l)).await()
    addGroupResult.isRight === true
    addGroupResult.value
  }

  def createContactsBook(): Long = {
    val createContactsBookResult = contactsBooksDAO.add(ContactsBook(0L, new DateTime().getMillis)).await()
    createContactsBookResult.isRight === true
    createContactsBookResult.value
  }

  def updateContact(contact: Contact) = {
    val updateContactResult = contactsDAO.update(contact).await()
    updateContactResult.isRight === true
  }

}
