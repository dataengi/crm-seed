package com.dataengi.crm.contacts.slick.tables

import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType

trait PhoneTableDescription extends ContactsTableDescription {

  import profile.api._

  case class PhoneRow(id: Long, phoneType: ContactFieldType, phone: String, contactId: Long)

  class PhoneTable(tag: Tag) extends Table[PhoneRow](tag, "phone_contact") {

    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def phoneType = column[ContactFieldType]("type")
    def phone     = column[String]("phone")

    def contactId = column[Long]("contact_id")

    def * = (id, phoneType, phone, contactId) <> (PhoneRow.tupled, PhoneRow.unapply)

  }

  lazy val Phones = TableQuery[PhoneTable]
}
