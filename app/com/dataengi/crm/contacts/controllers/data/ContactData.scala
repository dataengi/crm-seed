package com.dataengi.crm.contacts.controllers.data

import com.dataengi.crm.contacts.models._
import com.dataengi.crm.contacts.models.ContactTypes._

case class ContactData(name: String,
                       contactsBookId: Long,
                       createDate: Long,
                       emails: List[Email] = List(),
                       groups: List[Group] = List(),
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
                       advertiserId: Option[Long],
                       id: Option[Long] = None)

object ContactData {

  def apply(contact: Contact, groups: List[Group]): ContactData =
    new ContactData(contact.name,
                    contact.contactsBookId,
                    contact.createDate,
                    contact.emails,
                    groups,
                    contact.phones,
                    contact.skypeId,
                    contact.fax,
                    contact.company,
                    contact.jobPosition,
                    contact.address,
                    contact.timeZone,
                    contact.language,
                    contact.contactType,
                    contact.note,
                    contact.advertiserId,
                    contact.id)

}
