package com.dataengi.crm.identities.formatters.authentication

import com.dataengi.crm.identities.controllers.data.SignUpData
import play.api.libs.json.{JsValue, Json, OFormat}

trait SignUpFormatter {

  implicit val signUpDataFormatter: OFormat[SignUpData] = Json.format[SignUpData]

}

object SignUpFormatter extends SignUpFormatter