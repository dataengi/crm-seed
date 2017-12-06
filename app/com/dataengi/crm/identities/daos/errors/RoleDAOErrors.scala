package com.dataengi.crm.identities.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class RoleDAOError(description: String) extends AppErrorResult

object RoleDAOErrors {

  def roleWithIdNotFound(id: Long) = RoleDAOError(s"Role with $id not found in roles table")

  val RoleNotFound     = RoleDAOError("Role not found in roles table")
  val RoleAlreadyExist = RoleDAOError("Role has already existed")
  val RoleIdIsEmpty    = RoleDAOError("Can not do action because role doesn't exist")

}
