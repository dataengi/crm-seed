package com.dataengi.crm.identities.modules

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Environment => PlayEnv}

import scala.concurrent.Future
import scala.language.{higherKinds, reflectiveCalls}

class CustomSecuredErrorHandler extends SecuredErrorHandler {

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
    Future.successful(Unauthorized(Json.obj("message" -> "Not Authenticated")))
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
    Future.successful(Forbidden(Json.obj("message" -> "Not Authorized")))
  }
}