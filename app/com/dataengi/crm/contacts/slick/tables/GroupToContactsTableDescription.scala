package com.dataengi.crm.contacts.slick.tables

trait GroupToContactsTableDescription extends GroupTableDescription with ContactsTableDescription {

  import profile.api._

  case class GroupToContactRow(groupId: Long, contactId: Long)

  class GroupToContactsTable(tag: Tag) extends Table[GroupToContactRow](tag, "group_to_contacts") {

    def groupId   = column[Long]("group_id")
    def contactId = column[Long]("contact_id")

    def pk = primaryKey("pk", (groupId, contactId))

    def * = (groupId, contactId) <> (GroupToContactRow.tupled, GroupToContactRow.unapply)

  }

  lazy val GroupToContacts = TableQuery[GroupToContactsTable]

}
