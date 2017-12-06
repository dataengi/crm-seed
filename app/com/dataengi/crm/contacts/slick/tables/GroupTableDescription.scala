package com.dataengi.crm.contacts.slick.tables

trait GroupTableDescription extends ContactsBookTableDescription {

  import profile.api._

  case class GroupRow(id: Long, name: String, contactsBookId: Long, createDate: Long)

  class GroupTable(tag: Tag) extends Table[GroupRow](tag, "groups_contact") {

    def id             = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name           = column[String]("name")
    def contactsBookId = column[Long]("contacts_book_id")
    def createDate     = column[Long]("create_date")

    def * = (id, name, contactsBookId, createDate) <> (GroupRow.tupled, GroupRow.unapply)

  }

  lazy val Groups = TableQuery[GroupTable]

}
