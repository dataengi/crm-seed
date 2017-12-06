package com.dataengi.crm.contacts.services.errors

import com.dataengi.crm.common.context.types.{AppErrorResult, _}
import com.dataengi.crm.common.errors.ValueNotFound

object GroupsServiceErrors {

  val GroupNotFoundError = GroupsServiceError("Group not found")

  val MapErrors: (AppError) => AppError = {
    case ValueNotFound(id) => GroupNotFound(id)
    case error             => error
  }

}

sealed trait CommonGroupsServiceError extends AppErrorResult
case class GroupsServiceError(val description: String) extends CommonGroupsServiceError
case class GroupNotFound(id: Long) extends CommonGroupsServiceError {

  override val description: String = s"Group with $id not found"

}
