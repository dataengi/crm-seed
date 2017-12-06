package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.common.extensions.formatters.JsonFormatter
import com.dataengi.crm.contacts.models.ContactTypes
import com.dataengi.crm.contacts.models.ContactTypes.ContactType
import play.api.libs.json._

trait ContactTypesFormatter {

  implicit val contactTypeFormatter: Format[ContactType] = JsonFormatter.enumFormat(ContactTypes)

}

object ContactTypesFormatter extends ContactTypesFormatter
