package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.contacts.models.ContactsBook
import play.api.libs.json.{JsValue, Json}

trait ContactsBookFormatter extends ContactFormatter {

  implicit val contactsBookFormatter = Json.format[ContactsBook]

}

object ContactsBookFormatter extends ContactsBookFormatter