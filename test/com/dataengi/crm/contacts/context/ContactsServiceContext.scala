package com.dataengi.crm.contacts.context

import com.dataengi.crm.contacts.models.Contact
import com.dataengi.crm.contacts.services.{ContactsBooksService, ContactsService, GroupsService}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.contacts.daos.arbitraries.ContactsArbitrary
import com.dataengi.crm.identities.context.UsersContext

trait ContactsServiceContext extends UsersContext with ContactsArbitrary {

  lazy val contactsService: ContactsService           = application.injector.instanceOf[ContactsService]
  lazy val contactsBooksService: ContactsBooksService = application.injector.instanceOf[ContactsBooksService]
  lazy val groupsService: GroupsService               = application.injector.instanceOf[GroupsService]

  def ignoreIds(contact: Contact): Contact = {
    contact.copy(
      emails = contact.emails.map(_.copy(id = None)),
      phones = contact.phones.map(_.copy(id = None)),
      address = contact.address.map(_.copy(id = None))
    )
  }

  def createContact(): Contact = {
    val createContactData = createContactArbitrary.arbitrary.sample.get
    val createResult      = contactsService.create(createContactData, rootUser.id.get).await()
    createResult.value
  }

  def createContact(creatorUserId: Long) = {
    val createContactData = createContactArbitrary.arbitrary.sample.get
    val createResult      = contactsService.create(createContactData, creatorUserId).await()
    createResult.value
  }

}
