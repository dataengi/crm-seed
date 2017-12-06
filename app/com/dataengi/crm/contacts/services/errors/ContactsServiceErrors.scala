package com.dataengi.crm.contacts.services.errors

import com.dataengi.crm.common.context.types._

object ContactsServiceErrors {

  def contactNotFound(id: Int) = ContactNotFound(id)

}

sealed trait ContactsServiceError extends AppErrorResult
case class ContactNotFound(id: Int) extends ContactsServiceError {

  override val description: String = s"Contacts with $id not found"

}
