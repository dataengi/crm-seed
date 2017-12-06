package com.dataengi.crm.contacts.slick.tables

import com.dataengi.crm.identities.slick.tables.identities.UsersTableDescription


trait ContactsBookTableDescription extends UsersTableDescription {

  import profile.api._

  case class ContactsBookRow(id: Long, ownerId: Long, createDate: Long)

  class ContactsBookTable(tag: Tag) extends Table[ContactsBookRow](tag, "contacts_book") {

    def id         = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def ownerId    = column[Long]("owner_id")
    def createDate = column[Long]("create_date")

    def * = (id, ownerId, createDate) <> (ContactsBookRow.tupled, ContactsBookRow.unapply)

  }

  lazy val ContactsBooks = TableQuery[ContactsBookTable]

}
