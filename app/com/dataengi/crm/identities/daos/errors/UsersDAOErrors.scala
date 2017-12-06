package com.dataengi.crm.identities.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class UsersDAOErrors(description: String) extends Exception(description) with AppErrorResult

object UsersDAOErrors {

  val LoginInfoNotFound = UsersDAOErrors("LoginInfo not found in users table")
  val UserNotFound      = UsersDAOErrors("User not found in users table")
  val RoleNotFound      = UsersDAOErrors("Role not found in roles table")
  val CompanyNotFound   = UsersDAOErrors("Company not found in companies table")
  val AuthInfoNotFound  = UsersDAOErrors("AuthInfo not found in auth_info table")
  val UserAlreadyExist  = UsersDAOErrors("User has already existed")
  val UserIdIsEmpty     = UsersDAOErrors("Can not do action because user doesn't exist")

}
