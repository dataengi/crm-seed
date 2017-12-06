package com.dataengi.crm.identities.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class InvitesDAOError(description: String) extends AppErrorResult

object InvitesDAOErrors {

  val InviteNotFound = InvitesDAOError("Invite not found in invites table")
  val InviteAlreadyExist = InvitesDAOError("Invite has already existed")
  val InviteIdIsEmpty = InvitesDAOError("Can not do action because invite doesn't exist")

}
