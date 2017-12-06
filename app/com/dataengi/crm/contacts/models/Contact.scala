package com.dataengi.crm.contacts.models

import com.dataengi.crm.contacts.models.ContactTypes.ContactType

case class Contact(name: String,
                   contactsBookId: Long,
                   createDate: Long,
                   emails: List[Email] = List(),
                   groupIds: List[Long] = List(),
                   phones: List[Phone] = List(),
                   skypeId: Option[String] = None,
                   fax: Option[String] = None,
                   company: Option[String] = None,
                   jobPosition: Option[String] = None,
                   address: Option[Address] = None,
                   timeZone: Option[String] = None,
                   language: Option[String] = None,
                   contactType: Option[ContactType] = None,
                   note: Option[String] = None,
                   id: Option[Long] = None,
                   advertiserId: Option[Long] = None)
