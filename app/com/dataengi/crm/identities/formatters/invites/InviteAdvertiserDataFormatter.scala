package com.dataengi.crm.identities.formatters.invites

import com.dataengi.crm.identities.controllers.data.InviteAdvertiserData
import play.api.libs.json.{Json, OFormat}

trait InviteAdvertiserDataFormatter {
  implicit val inviteAdvertiserDataFormatter: OFormat[InviteAdvertiserData] = Json.format[InviteAdvertiserData]
}

object InviteAdvertiserDataFormatter extends InviteAdvertiserDataFormatter
