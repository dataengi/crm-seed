package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.contacts.models.Phone
import play.api.libs.json.{JsValue, Json, OFormat}

trait PhoneFormatter extends ContactsFiledFormatter {

  implicit val phoneFormatter: OFormat[Phone] = Json.format[Phone]

}

object PhoneFormatter extends PhoneFormatter