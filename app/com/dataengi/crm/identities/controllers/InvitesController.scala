package com.dataengi.crm.identities.controllers

import java.util.UUID

import com.dataengi.crm.identities.errors.InvitesControllerErrors
import cats.instances.all._
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.controllers.ApplicationController
import com.dataengi.crm.identities.actions.SecuredFilteredAction
import com.dataengi.crm.identities.errors.AuthenticationServiceErrors
import com.dataengi.crm.identities.formatters.invites.InviteFormatter._
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.dataengi.crm.identities.formatters.invites.{InviteAdvertiserDataFormatter, InviteDataFormatter}
import com.dataengi.crm.identities.models.{Invite, InviteStatuses}
import com.dataengi.crm.identities.services.{AuthenticationService, InvitesService}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import com.dataengi.crm.identities.utils.auth.DefaultEnv
import com.dataengi.crm.identities.views.html
import com.dataengi.crm.identities.views.html.error

import scala.concurrent.ExecutionContext

class InvitesController @Inject()(invitesService: InvitesService,
                                  authenticationService: AuthenticationService,
                                  val silhouette: Silhouette[DefaultEnv],
                                  components: ControllerComponents,
                                  implicit val executionContext: ExecutionContext)
    extends ApplicationController(components)
    with SecuredFilteredAction {

  def create() = SecuredAccessAction.async(parse.json(InviteDataFormatter.inviteDataFormatter)) { implicit request =>
    invitesService.create(request.body, request.identity.id.get)
  }

  def createAdvertiserInvite() =
    SecuredAccessAction.async(parse.json(InviteAdvertiserDataFormatter.inviteAdvertiserDataFormatter)) { implicit request =>
      invitesService.createAdvertiserInvite(request.body, request.identity.company.id.get, request.identity.id.get)
    }

  def getByCompany = SecuredAccessAction.async { implicit request =>
    for {
      companyId <- request.identity.company.id.toOrWithLeftError(InvitesControllerErrors.CompanyIdIsEmpty)
      invites   <- invitesService.getByCompanyId(companyId)
    } yield invites
  }

  def toSignUp(hash: UUID): Action[AnyContent] = Action.async {
    val getSignUpUrl = for {
      invite            <- invitesService.getByHash(hash).flatMap(checkStatus)
      redirectInviteUrl <- invitesService.getInviteUrl(invite)
    } yield Redirect(redirectInviteUrl)
    //    getSignUpUrl.asyncResult
    getSignUpUrl.value.map {
      case Right(result: Result) => result
      case Left(appErrorResult: ForbiddenResult) =>
        Ok(error(403, "URL is not active", appErrorResult.description))
      case Left(appErrorResult: AppErrorResult) =>
        Ok(html.error(403, "URL is not active", appErrorResult.description))
      case Left(appErrorResult: AppError) =>
        Ok(html.error(403, "URL is not active", appErrorResult.toString))
    }
  }

  private def checkStatus(invite: Invite): Or[Invite] =
    if (invite.status == InviteStatuses.Waiting)
      invite.toOr
    else
      AuthenticationServiceErrors.SignUpUrlAlreadyUsed.toErrorOrWithType[Invite]

  def all = SecuredAccessAction.async { implicit request =>
    invitesService.all()
  }

  def getByInvitedUser(invitedBy: Long) = SecuredAccessAction.async { implicit request =>
    invitesService.getByUserIdWhichInvite(invitedBy)
  }

  def remove(id: Long) = SecuredAccessAction.async { implicit request =>
    invitesService.remove(id)
  }

  def checkExistUser(email: String) = SecuredAccessAction.async { implicit request =>
    authenticationService.checkUserExist(email)
  }
}
