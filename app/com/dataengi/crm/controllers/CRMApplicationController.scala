package com.dataengi.crm.controllers

import javax.inject._

import controllers.{Assets, BuildInfo}
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class CRMApplicationController @Inject()(components: ControllerComponents) extends AbstractController(components) {

  def version = Action {
    Ok("App version: " + BuildInfo.version)
  }

  def versionJson = Action {
    Ok(Json.obj("version" -> BuildInfo.version))
  }

}
