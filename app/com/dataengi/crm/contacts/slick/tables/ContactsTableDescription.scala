package com.dataengi.crm.contacts.slick.tables

import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType
import com.dataengi.crm.contacts.models.ContactTypes.ContactType
import com.dataengi.crm.contacts.models.{ContactFieldTypes, ContactTypes}
import slick.lifted.ProvenShape

trait ContactsTableDescription extends ContactsBookTableDescription {

  import profile.api._

  case class ContactRow(id: Long,
                        name: String,
                        contactsBookId: Long,
                        createDate: Long,
                        skypeId: Option[String],
                        fax: Option[String],
                        company: Option[String],
                        jobPosition: Option[String],
                        addressId: Option[Long],
                        timeZone: Option[String],
                        language: Option[String],
                        contactType: Option[ContactType],
                        note: Option[String],
                        advertiserId: Option[Long])

  implicit val contactTypeMapper: BaseColumnType[ContactType] = enumColumnMapper(ContactTypes)

  implicit val contactFieldTypeMapper: BaseColumnType[ContactFieldType] = enumColumnMapper(ContactFieldTypes)

  class ContactTable(tag: Tag) extends Table[ContactRow](tag, "contacts") {

    def id             = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name           = column[String]("name")
    def contactsBookId = column[Long]("contacts_book_id")
    def createDate     = column[Long]("create_date")
    def skypeId        = column[Option[String]]("skype_id")
    def fax            = column[Option[String]]("fax")
    def company        = column[Option[String]]("company")
    def jobPosition    = column[Option[String]]("job_position")
    def addressId      = column[Option[Long]]("address_id")
    def timeZone       = column[Option[String]]("time_zone")
    def language       = column[Option[String]]("language")
    def contactType    = column[Option[ContactType]]("contact_type")
    def note           = column[Option[String]]("note")
    def advertiserId   = column[Option[Long]]("advertiser")

    def * : ProvenShape[ContactRow] =
      (id,
       name,
       contactsBookId,
       createDate,
       skypeId,
       fax,
       company,
       jobPosition,
       addressId,
       timeZone,
       language,
       contactType,
       note,
       advertiserId) <> (ContactRow.tupled, ContactRow.unapply)

  }

  val Contacts = TableQuery[ContactTable]

}
