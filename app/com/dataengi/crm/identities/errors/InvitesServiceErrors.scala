package com.dataengi.crm.identities.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class InviteServiceError(description: String) extends AppErrorResult

object InvitesServiceErrors {

  def invalidUUIDError(invalidUUID: String) = InviteServiceError(s"Invalid UUID=$invalidUUID")

  val CanNotUpdateInviteWithoutId = InviteServiceError(s"Can't update invite without id")

  val CanNotCreateInviteByRootWithoutCompanyId = InviteServiceError("Can not create invite by root without company id")

  val CanNotFindAdvertiserRole = InviteServiceError("Can not find advertiser role")

}
