package com.dataengi.crm.contacts.context

import com.dataengi.crm.contacts.daos.arbitraries.ContactsArbitrary

trait GroupsServiceContext extends ContactsServiceContext with ContactsArbitrary {

  val TestGroupName = "TestGroupName"

}
