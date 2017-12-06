package com.dataengi.crm.identities.repositories.errors

import java.util.UUID

import com.dataengi.crm.common.context.types.AppErrorResult
import com.dataengi.crm.identities.errors.InviteServiceError


object InvitesRepositoryErrors {

  def InviteWithHashNotExist(hash: UUID) = InvitesRepositoryError(s"Invite with hash: $hash not exist")

  val InviteAlreadyExist = InviteServiceError("Invite with this email already exist")
}

case class InvitesRepositoryError(description: String) extends AppErrorResult
