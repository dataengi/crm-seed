package com.dataengi.crm.identities.errors

import com.dataengi.crm.common.context.types.AppErrorResult
import com.dataengi.crm.identities.models.Role

case class RolesServiceError(description: String) extends AppErrorResult

object RolesServiceErrors {

  def roleWithNameNotFound(name: String) = RolesServiceError(s"Role with name $name not found")

  val RolesAlreadyExist = RolesServiceError("Role with this name already exist")

  val ReadingRolesPermissionDenied = RolesServiceError(s"You haven't permission to get all roles")

}
