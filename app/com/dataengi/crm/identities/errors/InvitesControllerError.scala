package com.dataengi.crm.identities.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class InvitesControllerError(description: String) extends AppErrorResult

object InvitesControllerErrors {
  val CompanyIdIsEmpty = InvitesControllerError("Can not get company id from None")
}
