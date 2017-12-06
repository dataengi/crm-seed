package com.dataengi.crm.contacts.controllers.data

import com.dataengi.crm.contacts.models.ContactFieldTypes._
import com.dataengi.crm.contacts.models.ContactTypes._

case class CreateContactData(name: String,
                             phones: List[PhoneData] = List(),
                             emails: List[EmailData] = List(),
                             address: Option[AddressData] = None,
                             skypeId: Option[String] = None,
                             fax: Option[String] = None,
                             company: Option[String] = None,
                             jobPosition: Option[String] = None,
                             timeZone: Option[String] = None,
                             language: Option[String] = None,
                             contactType: Option[ContactType] = None,
                             note: Option[String] = None)

case class PhoneData(phoneType: ContactFieldType, phone: String)

case class EmailData(emailType: ContactFieldType, email: String)

case class AddressData(street: Option[String],
                       state: Option[String],
                       country: Option[String],
                       city: Option[String],
                       zipCode: Option[String],
                       id: Option[Long])
