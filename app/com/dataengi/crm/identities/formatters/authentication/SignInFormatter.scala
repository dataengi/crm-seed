package com.dataengi.crm.identities.formatters.authentication

import com.dataengi.crm.identities.controllers.data.SignInData
import com.dataengi.crm.identities.services.{SignInInfo, SignInResult}
import play.api.libs.json.{Json, OFormat}

trait SignInFormatter extends IdentityFormatter {

  implicit val signInFormatter: OFormat[SignInData] = Json.format[SignInData]
  implicit val signInInfoFormatter: OFormat[SignInInfo] = Json.format[SignInInfo]
  implicit val signInResultFormatter: OFormat[SignInResult] = Json.format[SignInResult]

}

object SignInFormatter extends SignInFormatter