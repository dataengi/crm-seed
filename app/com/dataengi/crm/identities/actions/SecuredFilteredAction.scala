package com.dataengi.crm.identities.actions

import com.mohiva.play.silhouette.api.Silhouette
import com.dataengi.crm.identities.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

trait SecuredFilteredAction {

  implicit val silhouette: Silhouette[DefaultEnv]
  implicit val executionContext: ExecutionContext

  val SecuredAccessAction = silhouette.SecuredAction.andThen(PermissionAction.UserAccessFilter())

}
