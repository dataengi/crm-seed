package com.dataengi.crm.contacts.controllers.data

import com.dataengi.crm.contacts.models.ContactTypes._

case class UpdateContactData(id: Long,
                             name: String,
                             contactsBookId: Long,
                             groupIds: List[Long] = List(),
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
                             note: Option[String],
                             advertiserId: Option[Long])
