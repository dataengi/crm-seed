package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.contacts.models.Address
import play.api.libs.json.{Json, OFormat}

object AddressFormatter extends AddressFormatter

trait AddressFormatter {
  implicit val addressFormat: OFormat[Address] = Json.format[Address]
}
