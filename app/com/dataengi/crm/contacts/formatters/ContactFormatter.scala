package com.dataengi.crm.contacts.formatters

import com.dataengi.crm.contacts.controllers.data._
import com.dataengi.crm.contacts.models.Contact
import play.api.libs.json._

object ContactFormatter extends ContactFormatter

trait ContactFormatter
    extends AddressFormatter
    with EmailFormatter
    with PhoneFormatter
    with GroupFormatter
    with ContactsFiledFormatter
    with ContactTypesFormatter {

  implicit val contactFormatter: OFormat[Contact]                               = Json.format[Contact]
  implicit val addressDataFormatter: OFormat[AddressData]                       = Json.format[AddressData]
  implicit val emailDataFormatter: OFormat[EmailData]                           = Json.format[EmailData]
  implicit val phoneDataFormatter: OFormat[PhoneData]                           = Json.format[PhoneData]
  implicit val createContactDataFormatter: OFormat[CreateContactData]           = Json.format[CreateContactData]
  implicit val updateContactDataFormatter: OFormat[UpdateContactData]           = Json.format[UpdateContactData]
  implicit val createGroupFormatter: OFormat[CreateGroupData]                   = Json.format[CreateGroupData]
  implicit val addContactsToGroupDataFormatter: OFormat[AddContactsToGroupData] = Json.format[AddContactsToGroupData]
  implicit val leaveGroupDataFormatter: OFormat[LeaveGroupData]                 = Json.format[LeaveGroupData]
  implicit val updateGroupDataFormatter: OFormat[UpdateGroupData]               = Json.format[UpdateGroupData]
  implicit val removeContactsDataFormatter: OFormat[RemoveContactsData]         = Json.format[RemoveContactsData]
  implicit val contactDataFormatter: OFormat[ContactData]                       = Json.format[ContactData]
  implicit val fullContactsBookDataFormatter: OFormat[FullContactsBookData]     = Json.format[FullContactsBookData]
  implicit val getContactsBooksDataFormatter: OFormat[GetContactsBooksData]     = Json.format[GetContactsBooksData]

}
