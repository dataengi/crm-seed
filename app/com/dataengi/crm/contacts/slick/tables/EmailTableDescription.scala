package com.dataengi.crm.contacts.slick.tables

import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType

trait EmailTableDescription extends ContactsTableDescription {

  import profile.api._

  case class EmailRow(id: Long, emailType: ContactFieldType, email: String, contactId: Long)

  class EmailTable(tag: Tag) extends Table[EmailRow](tag, "email_contact") {

    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def emailType = column[ContactFieldType]("type")
    def email     = column[String]("email")
    def contactId = column[Long]("contact_id")

    def * = (id, emailType, email, contactId) <>(EmailRow.tupled, EmailRow.unapply)

  }

  lazy val Emails = TableQuery[EmailTable]

}
