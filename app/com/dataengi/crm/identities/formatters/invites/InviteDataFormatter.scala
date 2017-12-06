package com.dataengi.crm.identities.formatters.invites

import com.dataengi.crm.identities.controllers.data.InviteData
import play.api.libs.json.{Json, OFormat}

trait InviteDataFormatter {
  implicit val inviteDataFormatter: OFormat[InviteData] = Json.format[InviteData]
}

object InviteDataFormatter extends InviteDataFormatter
