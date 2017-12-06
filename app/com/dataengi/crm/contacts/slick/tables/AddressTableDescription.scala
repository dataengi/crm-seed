package com.dataengi.crm.contacts.slick.tables

trait AddressTableDescription extends ContactsTableDescription {

  import profile.api._

  case class AddressRow(id: Long,
                        street: Option[String],
                        state: Option[String],
                        country: Option[String],
                        city: Option[String],
                        zipCode: Option[String])

  class AddressTable(tag: Tag) extends Table[AddressRow](tag, "address_contact") {

    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def street    = column[Option[String]]("street")
    def state     = column[Option[String]]("state")
    def country   = column[Option[String]]("country")
    def city      = column[Option[String]]("city")
    def zipCode   = column[Option[String]]("zipCode")

    def * = (id, street, state, country, city, zipCode) <> (AddressRow.tupled, AddressRow.unapply)

  }

  lazy val Addresses = TableQuery[AddressTable]

}
