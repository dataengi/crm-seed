package com.dataengi.crm.identities.formatters.permissions

import com.dataengi.crm.common.extensions.formatters.JsonFormatter
import com.dataengi.crm.identities.models.PermissionStates.PermissionState
import com.dataengi.crm.identities.models.{Actions, Permission, PermissionStates}
import play.api.libs.json._

trait PermissionFormatter {

  implicit val actionFormat = JsonFormatter.enumFormat(Actions)

  implicit val permissionStateFormat: Format[PermissionState] = JsonFormatter.enumFormat(PermissionStates)

  implicit val permissionFormatter: OFormat[Permission] = Json.format[Permission]

}
