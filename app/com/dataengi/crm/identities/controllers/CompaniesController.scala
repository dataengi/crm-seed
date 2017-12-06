package com.dataengi.crm.identities.controllers

import com.dataengi.crm.common.controllers.ApplicationController
import com.dataengi.crm.identities.actions.SecuredFilteredAction
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.dataengi.crm.identities.formatters.companies.CompaniesFormatter._
import com.dataengi.crm.identities.services.CompaniesService
import play.api.i18n.I18nSupport
import play.api.mvc.ControllerComponents
import com.dataengi.crm.identities.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

class CompaniesController @Inject()(companiesService: CompaniesService,
                                    val silhouette: Silhouette[DefaultEnv],
                                    components: ControllerComponents,
                                    implicit val executionContext: ExecutionContext)
    extends ApplicationController(components)
    with SecuredFilteredAction
    with I18nSupport {

  def all = SecuredAccessAction.async { implicit request =>
    companiesService.all()
  }

  def get(id: Long) = SecuredAccessAction.async { implicit request =>
    companiesService.get(id)
  }

  def findByName(name: String) = SecuredAccessAction.async { implicit request =>
    companiesService.find(name)
  }

  def create = SecuredAccessAction.async(parse.json(companyDataFormatter)) { implicit request =>
    companiesService.create(request.body)
  }

}
