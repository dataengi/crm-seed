package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.common.extensions.formatters.JsonFormatter
import com.dataengi.crm.contacts.models.ContactFieldTypes
import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType
import play.api.libs.json._

trait ContactsFiledFormatter {

  implicit val contactFieldTypeFormatter: Format[ContactFieldType] = JsonFormatter.enumFormat(ContactFieldTypes)

}

object ContactsFiledFormatter extends ContactsFiledFormatter
