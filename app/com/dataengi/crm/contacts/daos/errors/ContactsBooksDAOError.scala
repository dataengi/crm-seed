package com.dataengi.crm.contacts.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class ContactsBooksDAOError(description: String) extends AppErrorResult

object ContactsBooksDAOErrors {

  val ContactsBookNotFound  = ContactsBooksDAOError("Contacts book not found in contacts book table")
  val ContactsBookIdIsEmpty = ContactsBooksDAOError("Can not do action because contacts book doesn't exist")
}
