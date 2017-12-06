package com.dataengi.crm.identities.actions.errors

import com.dataengi.crm.common.context.types.{AppErrorResult, ForbiddenResult, UnauthorizedResult}
import com.dataengi.crm.identities.models.Actions.Action

case class ActionError(description: String)      extends AppErrorResult
case class PermissionDenied(description: String) extends ForbiddenResult
case class FiltersError(description: String)     extends UnauthorizedResult

object ActionErrors {

  val UserDeactivatedError = FiltersError(s"User is deactivated")

  def actionNotFound(name: String) = ActionError(s"Action with name=$name not found")

  def permissionDenied(action: Action) = PermissionDenied(s"Permission for $action action denied for current user")

}
