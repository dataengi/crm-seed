package com.dataengi.crm.identities.slick.tables.identities

import com.dataengi.crm.identities.models.PermissionStates
import com.dataengi.crm.identities.models.PermissionStates.PermissionState
import com.dataengi.crm.identities.slick.tables.TableDescription

trait PermissionsTableDescription extends TableDescription {

  import profile.api._

  implicit val permissionStateMapper = MappedColumnType.base[PermissionState, Int](
    { (value: PermissionState) =>
      value.id
    }, { id: Int =>
      PermissionStates(id)
    }
  )

  case class PermissionRow(action: String, state: PermissionState, roleId: Option[Long], id: Long = 0l)

  class PermissionsTable(tag: Tag) extends Table[PermissionRow](tag, "permissions") {

    def id     = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def action = column[String]("action")
    def state  = column[PermissionState]("permission_state")
    def roleId = column[Long]("role_id")

    def * = (action, state, roleId.?, id) <> (PermissionRow.tupled, PermissionRow.unapply)

  }

  val Permissions = TableQuery[PermissionsTable]

}
