package com.dataengi.crm.identities.actions

import com.dataengi.crm.identities.actions.errors.ActionErrors
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import play.api.mvc.{ActionFilter, Result}
import com.dataengi.crm.identities.utils.auth.DefaultEnv
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.models.Actions.Action
import com.dataengi.crm.identities.models.{PermissionStates, UserStates, Users}

import scala.concurrent.{ExecutionContext, Future}

object PermissionAction {

  type AuthRequest[A] = SecuredRequest[DefaultEnv, A]

  case class PermissionFilter(action: Action)(implicit override val executionContext: ExecutionContext)
      extends ActionFilter[AuthRequest] {

    override protected def filter[A](request: AuthRequest[A]): Future[Option[Result]] = Future {
      if (checkPermission(request)) None
      else Some(ActionErrors.permissionDenied(action).result)
    }

    private def checkPermission[A](request: AuthRequest[A]): Boolean = {
      Users.userPermissions.get(request.identity).find(_.action == action).exists(_.state == PermissionStates.Allow)
    }

  }

  case class UserAccessFilter(implicit val executionContext: ExecutionContext) extends ActionFilter[AuthRequest] {

    val UserDeactivatedResult                 = Future.successful(Some(ActionErrors.UserDeactivatedError.result))
    val SuccessResult: Future[Option[Result]] = Future.successful(None)

    override protected def filter[A](request: AuthRequest[A]): Future[Option[Result]] =
      if (request.identity.state == UserStates.Deactivated) UserDeactivatedResult else SuccessResult
  }

}
