package com.dataengi.crm.contacts.models

import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType

case class Email(emailType: ContactFieldType, email: String, id: Option[Long] = None)
