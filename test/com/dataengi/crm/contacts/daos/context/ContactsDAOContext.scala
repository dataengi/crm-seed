package com.dataengi.crm.contacts.daos.context

import com.dataengi.crm.contacts.daos._
import com.dataengi.crm.contacts.models._
import com.dataengi.crm.contacts.context.ContactsServiceContext
import com.dataengi.crm.identities.daos.{UsersDAO, UsersSlickDAOImplementation}
import org.joda.time.DateTime

trait ContactsDAOContext extends ContactsServiceContext {

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[ContactsSlickDAO].to[ContactsSlickDAOImplementation],
      bind[ContactsBooksDAO].to[ContactsBooksSlickDAOImplementation],
      bind[GroupsDAO].to[GroupsSlickDAOImplementation],
      bind[UsersDAO].to[UsersSlickDAOImplementation]
    )
  }

  lazy val contactsDAO      = application.injector.instanceOf[ContactsSlickDAO]
  lazy val contactsBooksDAO = application.injector.instanceOf[ContactsBooksDAO]
  lazy val groupsDAO        = application.injector.instanceOf[GroupsDAO]
  lazy val usersDAO         = application.injector.instanceOf[UsersDAO]

  val testContactFullWithoutRealIds = Contact(
    name = "TestContact",
    contactsBookId = 1, //change to real id
    createDate = new DateTime().getMillis,
    emails = List(Email(ContactFieldTypes.Home, "php@gov.no"), Email(ContactFieldTypes.Work, "js@gov.no")),
    groupIds = List(), //fill with real ids
    phones = List(Phone(ContactFieldTypes.Home, "10203040"), Phone(ContactFieldTypes.Work, "40302010")),
    skypeId = Some("TestSkypeId"),
    fax = Some("TestFax"),
    company = Some("IndusLogic"),
    jobPosition = Some("sr.Developer"),
    address = Some(Address(Some("Pushkina"), None, None, None, None)),
    timeZone = Some("UTC+2"),
    language = Some("Scala"),
    contactType = Some(ContactTypes.Client),
    note = Some("Test_note"))

}
