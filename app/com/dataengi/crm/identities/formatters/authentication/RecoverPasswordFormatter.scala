package com.dataengi.crm.identities.formatters.authentication

import com.dataengi.crm.common.context.types.RecoverPasswordResultResponse
import play.api.libs.json.{Json, OFormat}

trait RecoverPasswordFormatter {

  implicit val signUpDataFormatter: OFormat[RecoverPasswordResultResponse] = Json.format[RecoverPasswordResultResponse]

}

object RecoverPasswordFormatter extends RecoverPasswordFormatter