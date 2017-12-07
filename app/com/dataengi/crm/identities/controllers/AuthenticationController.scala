package com.dataengi.crm.identities.controllers

import javax.inject.Inject

import cats.instances.all._
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.controllers.ApplicationController
import com.dataengi.crm.common.extensions.formatters.JsonFormatterExtension._
import com.dataengi.crm.identities.actions.SecuredFilteredAction
import com.dataengi.crm.identities.controllers.data.RecoverPasswordData
import com.dataengi.crm.identities.formatters.authentication.SignInFormatter._
import com.dataengi.crm.identities.controllers.swagger.AuthenticationApiSwaggerDescription
import com.mohiva.play.silhouette.api._
import com.dataengi.crm.identities.formatters.authentication.SignUpResultFormatter._
import com.dataengi.crm.identities.formatters.authentication.ForgotPasswordDataFormatter._
import com.dataengi.crm.identities.formatters.authentication.{ForgotPasswordDataFormatter, IdentityFormatter, SignInFormatter, SignUpFormatter}
import com.dataengi.crm.identities.services.{AuthenticationService, SignInInfo}
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc.{Action, ControllerComponents, Result}
import com.dataengi.crm.identities.utils.auth.DefaultEnv
import com.dataengi.crm.identities.views.html.error
import io.swagger.annotations._

import scala.concurrent.ExecutionContext

@Api("Authentication")
class AuthenticationController @Inject()(val silhouette: Silhouette[DefaultEnv],
                                         authenticationService: AuthenticationService,
                                         components: ControllerComponents,
                                         implicit val executionContext: ExecutionContext)
    extends ApplicationController(components)
      with AuthenticationApiSwaggerDescription
    with SecuredFilteredAction
    with  I18nSupport {

  @ApiImplicitParams(Array(new ApiImplicitParam(
    name = "body",
    dataType = "com.dataengi.crm.identities.controllers.data.SignInData",
    required = true,
    paramType = "body"
  )))
  def signIn = Action.async(parse.json(SignInFormatter.signInFormatter)) { implicit request =>
    authenticationService.signIn(request.body)(request).map { sigInResult: SignInInfo =>
      silhouette.env.eventBus.publish(LoginEvent(sigInResult.user, request))
      Ok(sigInResult.toJson)
    }
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(
    name = "body",
    dataType = "com.dataengi.crm.identities.controllers.data.SignUpData",
    required = true,
    paramType = "body"
  )))
  def signUp(inviteUUID: String) = Action.async(parse.json(SignUpFormatter.signUpDataFormatter)) { implicit request =>
    authenticationService.signUp(request.body, inviteUUID)(request).map { sigUpResult =>
      silhouette.env.eventBus.publish(SignUpEvent(sigUpResult.user, request))
      silhouette.env.eventBus.publish(LoginEvent(sigUpResult.user, request))
      Ok(sigUpResult.toJson(customSignUpResultWrites))
    }
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(
    name = "body",
    dataType = "com.dataengi.crm.identities.controllers.data.ForgotPassword",
    required = true,
    paramType = "body"
  )))
  def forgotPassword = Action.async(parse.json(ForgotPasswordDataFormatter.forgotPasswordFormatter)) { implicit request =>
    authenticationService.forgotPassword(request.body, request.host)
  }

  def toRecoverPassword(id: String) = Action.async { implicit request =>
    authenticationService.getRecoverPasswordUrl(id).map(url => Redirect(url)).value.map {
      case Right(result: Result)                => result
      case Left(appErrorResult: AppErrorResult) => Ok(error(403, "URL is not active", appErrorResult.description))
      case Left(appErrorResult: AppError)       => Ok(error(403, "URL is not active", appErrorResult.toString))
    }
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(
    name = "body",
    dataType = "com.dataengi.crm.identities.controllers.data.RecoverPasswordData",
    required = true,
    paramType = "body"
  )))
  def recoverPassword(recoverId: String): Action[RecoverPasswordData] = Action.async(parse.json(ForgotPasswordDataFormatter.recoverPasswordFormatter)) {
    implicit request =>
      authenticationService.recoverPassword(request.body, recoverId).map { recoverPasswordResult =>
        Ok(Json.obj("token" -> recoverPasswordResult))
      }
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def signOut = SecuredAccessAction.async { implicit request =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)// works incorrectly
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def identify = SecuredAccessAction.async { implicit request =>
    request.identity.toJson(IdentityFormatter.customIdentityWrites).toOr
  }

}
