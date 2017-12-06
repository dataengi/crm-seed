package com.dataengi.crm.identities.controllers

import com.dataengi.crm.identities.actions.SecuredFilteredAction
import com.dataengi.crm.identities.services.RolesService
import com.dataengi.crm.identities.formatters.roles.RoleFormatter._
import com.dataengi.crm.common.controllers.ApplicationController
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.I18nSupport
import play.api.mvc.ControllerComponents
import com.dataengi.crm.identities.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

class RolesController @Inject()(rolesService: RolesService,
                                val silhouette: Silhouette[DefaultEnv],
                                components: ControllerComponents,
                                implicit val executionContext: ExecutionContext)
    extends ApplicationController(components)
    with SecuredFilteredAction
    with I18nSupport {
  def all = SecuredAccessAction.async { implicit request =>
    rolesService.allRoles(request.identity)
  }

  def get(id: Long) = SecuredAccessAction.async { implicit request =>
    rolesService.get(id)
  }

  def findByName(name: String) = SecuredAccessAction.async { implicit request =>
    rolesService.find(name)
  }

}
