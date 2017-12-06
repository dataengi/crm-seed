package com.dataengi.crm.identities.formatters.invites

import com.dataengi.crm.common.extensions.formatters.JsonFormatter
import com.dataengi.crm.identities.formatters.roles.RoleFormatter
import com.dataengi.crm.identities.models.{Invite, InviteStatuses}
import play.api.libs.json._

trait InviteFormatter extends RoleFormatter {

  implicit val inviteStatusFormat = JsonFormatter.enumFormat(InviteStatuses)

  implicit val inviteFormatter: OFormat[Invite] = Json.format[Invite]
}

object InviteFormatter extends InviteFormatter