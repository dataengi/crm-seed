package com.dataengi.crm.common.errors

import com.dataengi.crm.common.context.types.AppErrorResult
import play.api.libs.json.JsError

case class JsonValidationError(jsError: JsError) extends AppErrorResult {

  val description: String = jsError.toString

}
