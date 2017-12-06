package com.dataengi.crm.common.context.types

import play.api.libs.json.{JsValue, Json}

trait AppErrorResult extends AppError {

  val description: String

  def toJson: JsValue = Json.obj("message" -> description)

}

trait ForbiddenResult extends AppErrorResult

trait UnauthorizedResult extends AppErrorResult
