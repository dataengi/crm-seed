package com.dataengi.crm.identities.formatters.authentication

import com.dataengi.crm.common.extensions.formatters.JsonFormatterExtension._
import com.dataengi.crm.identities.services.SignUpResult
import play.api.libs.json.{JsValue, Json, Writes}

trait SignUpResultFormatter extends IdentityFormatter {
  implicit val customSignUpResultWrites: Writes[SignUpResult] = new Writes[SignUpResult] {
    override def writes(o: SignUpResult): JsValue = Json.obj(
      "token" -> o.token,
      "user"  -> o.user.toJson(customIdentityWrites)
    )
  }
}

object SignUpResultFormatter extends SignUpResultFormatter
