package com.dataengi.crm.identities.formatters.authentication

import com.dataengi.crm.common.extensions.formatters.JsonFormatter
import com.dataengi.crm.identities.controllers.data.{ForgotPassword, RecoverPasswordData}
import com.dataengi.crm.identities.models.RecoverPasswordInfo
import com.dataengi.crm.identities.models.RecoverPasswordInfoStatuses
import com.dataengi.crm.identities.models.RecoverPasswordInfoStatuses.RecoverPasswordInfoStatus
import play.api.libs.json.{Format, JsValue, Json, OFormat}

trait ForgotPasswordDataFormatter {

  implicit val forgotPasswordFormatter: OFormat[ForgotPassword] = Json.format[ForgotPassword]

  implicit val recoverPasswordFormatter: OFormat[RecoverPasswordData] = Json.format[RecoverPasswordData]

  implicit val recoverPasswordInfoStatusFormatter: Format[RecoverPasswordInfoStatus] = JsonFormatter.enumFormat(RecoverPasswordInfoStatuses)

  implicit val recoverPasswordInfoFormatter: OFormat[RecoverPasswordInfo] = Json.format[RecoverPasswordInfo]

}

object ForgotPasswordDataFormatter extends ForgotPasswordDataFormatter