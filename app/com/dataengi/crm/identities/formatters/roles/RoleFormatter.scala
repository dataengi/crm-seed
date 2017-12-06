package com.dataengi.crm.identities.formatters.roles

import com.dataengi.crm.identities.formatters.permissions.PermissionFormatter
import com.dataengi.crm.identities.models.Role
import play.api.libs.json.{Json, OFormat}

trait RoleFormatter extends PermissionFormatter {

  implicit val roleFormatter: OFormat[Role] = Json.format[Role]

}

object RoleFormatter extends RoleFormatter
