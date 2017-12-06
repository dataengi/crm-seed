package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.contacts.models.Email
import play.api.libs.json.{JsValue, Json, OFormat}

trait EmailFormatter extends ContactsFiledFormatter {

  implicit val emailFormatter: OFormat[Email] = Json.format[Email]

}

object EmailFormatter extends EmailFormatter
