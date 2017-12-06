package com.dataengi.crm.identities.controllers

import com.dataengi.crm.identities.actions.SecuredFilteredAction
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.dataengi.crm.identities.services.UsersManagementService
import com.dataengi.crm.identities.formatters.authentication.IdentityFormatter._
import com.dataengi.crm.common.controllers.ApplicationController
import play.api.mvc.ControllerComponents
import play.api.i18n.I18nSupport
import com.dataengi.crm.identities.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

class UsersManagementController @Inject()(usersManagementService: UsersManagementService,
                                          val silhouette: Silhouette[DefaultEnv],
                                          components: ControllerComponents,
                                          implicit val executionContext: ExecutionContext)
    extends ApplicationController(components)
    with SecuredFilteredAction
    with I18nSupport {

  def getCompanyMembers(companyId: Long) = SecuredAccessAction.async { implicit request =>
    usersManagementService.getCompanyMembers(companyId, request.identity)
  }

  def getCompanyCurrentMembers = SecuredAccessAction.async { implicit request =>
    usersManagementService.getCompanyMembers(request.identity.company.id.get, request.identity)

  }

  def identityInfo = SecuredAccessAction.async { implicit request =>
    usersManagementService.identityInfo(request.identity)
  }

  def activateUser(userId: Long) = SecuredAccessAction.async { implicit request =>
    usersManagementService.activateUser(userId)
  }

  def deactivateUser(userId: Long) = SecuredAccessAction.async { implicit request =>
    usersManagementService.deactivateUser(userId)
  }

}
