package com.dataengi.crm.contacts.models

import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType

case class Phone(phoneType: ContactFieldType, phone: String, id: Option[Long] = None)
