package com.dataengi.crm.profiles.controllers

import com.dataengi.crm.identities.actions.SecuredFilteredAction
import cats.instances.all._
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.Silhouette
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.controllers.ApplicationController
import play.api.mvc.{AbstractController, ControllerComponents}
import com.dataengi.crm.profiles.formatters.ProfilesFormatter._
import com.dataengi.crm.profiles.services.ProfilesService
import com.dataengi.crm.identities.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

@Singleton
class ProfilesController @Inject()(profilesService: ProfilesService,
                                   val silhouette: Silhouette[DefaultEnv],
                                   components: ControllerComponents,
                                   implicit val executionContext: ExecutionContext)
  extends ApplicationController(components) with SecuredFilteredAction {

  def update = SecuredAccessAction.async(parse.json(updateProfileFormatter)) { implicit request =>
    profilesService.update(request.body, request.identity)
  }

  def get = SecuredAccessAction.async { implicit request =>
    profilesService.get(request.identity)
  }

  def getProfilesByUsers = SecuredAccessAction.async(parse.json(getUsersProfilesDataFormatter)) { implicit request =>
    profilesService.get(request.body)
  }

  def checkNickname(nickname: String) = SecuredAccessAction.async { implicit request =>
    profilesService.existProfileWithNickname(nickname)
  }

}
