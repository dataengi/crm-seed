package com.dataengi.crm.identities.models

import com.dataengi.crm.identities.models.Actions.Action
import com.dataengi.crm.identities.models.PermissionStates.PermissionState

case class Permission(action: Action, state: PermissionState, id: Option[Long] = None)

object Permissions {

  def allowed(action: Action) = Permission(action, PermissionStates.Allow)

  def denied(action: Action) = Permission(action, PermissionStates.Deny)

}
