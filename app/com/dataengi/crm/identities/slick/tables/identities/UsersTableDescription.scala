package com.dataengi.crm.identities.slick.tables.identities

import com.dataengi.crm.identities.models.UserStates
import com.dataengi.crm.identities.models.UserStates.UserState
import com.dataengi.crm.identities.slick.tables.credentionals.LoginInfoTableDescription

trait UsersTableDescription extends LoginInfoTableDescription with RolesTableDescription with CompaniesTableDescription {

  import profile.api._

  implicit val userStateMapper: BaseColumnType[UserState] = MappedColumnType.base[UserState, Int](
    { (value: UserState) =>
      value.id
    }, { (id: Int) =>
      UserStates(id)
    }
  )

  case class UserRow(loginInfoId: Long, companyId: Long, roleId: Long, state: UserState, id: Long = 0l)

  class UsersTable(tag: Tag) extends Table[UserRow](tag, "users") {

    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def loginInfoId = column[Long]("login_info_id")
    def companyId   = column[Long]("company_id")
    def roleId      = column[Long]("role_id")
    def state       = column[UserState]("state")

    def loginInfo =
      foreignKey("login_info", loginInfoId, LoginInfoTableQuery)(_.id,
                                                                 onUpdate = ForeignKeyAction.Cascade,
                                                                 onDelete = ForeignKeyAction.Cascade)

    def * = (loginInfoId, companyId, roleId, state, id) <> (UserRow.tupled, UserRow.unapply)

  }

  lazy val Users = TableQuery[UsersTable]

}
