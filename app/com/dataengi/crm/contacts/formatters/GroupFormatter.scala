package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.contacts.models.Group
import play.api.libs.json.{JsValue, Json, OFormat}

trait GroupFormatter {

  implicit val groupsFormatter: OFormat[Group] = Json.format[Group]

}

object GroupFormatter extends GroupFormatter
