package com.dataengi.crm.identities.slick.tables.identities

trait RolesTableDescription extends PermissionsTableDescription {

  import profile.api._

  case class RoleRow(name: String, id: Long = 0l)

  class RolesTable(tag: Tag) extends Table[RoleRow](tag, "roles") {

    def id   = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (name, id) <> (RoleRow.tupled, RoleRow.unapply)

  }

  val Roles = TableQuery[RolesTable]

}
