package com.dataengi.crm.contacts.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class ContactDAOError(description: String) extends AppErrorResult

object ContactsDAOErrors {

  def contactWithIdNotFound(id: Long) = ContactDAOError(s"Contact with id: $id not found in contacts table")

  val ContactIdIsEmpty    = ContactDAOError("Can not do action because contact id is empty")


}
