package com.dataengi.crm.contacts.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class GroupsDAOError(description: String) extends AppErrorResult

object GroupsDAOErrors {

  val GroupNotFound = GroupsDAOError("Group not found in groups table")
  val GroupIdIsEmpty = GroupsDAOError("Can not do action because group id is empty")

}
