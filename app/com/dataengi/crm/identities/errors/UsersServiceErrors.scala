package com.dataengi.crm.identities.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class UsersServiceError(description: String) extends AppErrorResult

object UsersServiceErrors {

  val IdentityNotFound = UsersServiceError("User with this login info not found")

}
