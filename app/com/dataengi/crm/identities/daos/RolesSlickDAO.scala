package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.daos.AutoIncBaseDAO
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.errors.RoleDAOErrors
import com.dataengi.crm.identities.models.{Actions, Permission, Role}
import com.dataengi.crm.identities.slick.tables.identities.RolesTableDescription
import com.google.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait RolesDAO extends AutoIncBaseDAO[Role] {

  def clearAllPermissions(): EmptyOr

  def find(name: String): Or[Option[Role]]

}

@Singleton
class RolesSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                            implicit val executionContext: ExecutionContext)
    extends RolesDAO
    with RolesTableDescription
    with RolesQueries {

  override def get(id: Long): Or[Role] =
    db.run(selectRole(id)).map(_.headOption).toOrWithLeft(RoleDAOErrors.roleWithIdNotFound(id))

  override def getOption(id: Long): Or[Option[Role]] =
    db.run(selectRole(id)).map(_.headOption).toOr

  override def add(role: Role): Or[Long] = db.run(insertRoleAction(role)).toOr

  override def update(role: Role) = role.id match {
    case Some(id) => db.run(updateRoleAction(id, role)).toEmptyOr
    case None     => RoleDAOErrors.RoleIdIsEmpty.toErrorOr
  }

  override def delete(id: Long): Or[Unit] = db.run(deleteRoleAction(id)).toEmptyOr

  override def find(name: String) = db.run(selectRoleByName(name)).map(_.headOption).toOr

  override def add(values: List[Role]): Or[List[Long]] = db.run(insertRolesAction(values)).toOr

  override def all: Or[List[Role]] = db.run(roleAction).toOr

  override def clearAllPermissions(): EmptyOr = db.run(deletePermissions).toEmptyOr
}

trait RolesQueries extends RolesTableDescription {

  import profile.api._

  def mapPermissions(permission: Permission, roleId: Long): PermissionRow =
    PermissionRow(action = permission.action.toString, state = permission.state, roleId = Some(roleId))

  def insertRoleAction(role: Role) =
    (for {
      roleId: Long      <- Roles returning Roles.map(_.id) += unMapRole(role)
      insertPermissions <- insertPermission(role, roleId)
    } yield roleId).transactionally

  def insertRolesAction(roles: List[Role]) =
    DBIO.sequence(roles.map(insertRoleAction)).transactionally

  def insertPermission(role: Role, rolesId: Long) =
    Permissions returning Permissions.map(_.id) ++= role.permissions.map(mapPermissions(_, rolesId))

  def selectPermissionsByRoleId(roleId: Long) =
    Permissions.filter(_.roleId === roleId).result

  def deletePermissions = Permissions.delete

  def updateRoleAction(id: Long, role: Role) =
    for {
      updateResult      <- Roles.filter(_.id === id).update(unMapRole(role))
      removePermissions <- Permissions.filter(_.roleId === id).delete
      insertPermissions <- insertPermission(role, id)
    } yield updateResult

  def deleteRoleAction(id: Long) = Roles.filter(_.id === id).delete

//  def selectRole(id: Long) = (Roles filter (_.id === id) join Permissions on (_.id === _.roleId)).result.map(toRole)

  def selectRole(id: Long) =
    (for {
      (role, permissions) <- Roles filter (_.id === id) joinLeft Permissions on (_.id === _.roleId)
    } yield (role, permissions)).result.map(toRoleOption)

  def selectRoleByName(name: String) =
    (for {
      (role, permissions) <- Roles filter (_.name === name) joinLeft Permissions on (_.id === _.roleId)
    } yield (role, permissions)).result.map(toRoleOption)

  def roleAction =
    (for {
      (role, permissions) <- Roles joinLeft Permissions on (_.id === _.roleId)
    } yield (role, permissions)).result.map(toRoleOption)

  val mapRole: (RoleRow) => Role = raw => Role(raw.name, Seq.empty[Permission], Some(raw.id))

  val unMapRole: (Role) => RoleRow = role => RoleRow(role.name, role.id.getOrElse(0l))

  def toRoleOption(resSeqJoined: Seq[(RoleRow, Option[PermissionRow])]): List[Role] = {
    resSeqJoined.groupBy(_._1).map(mapRoleWithOptionPermissions).toList
  }

  def mapRoleWithOptionPermissions(roleAndPermissions: (RoleRow, Seq[(RoleRow, Option[PermissionRow])])): Role =
    roleAndPermissions match {
      case (roleRole, rolesAndPermissions) =>
        Role(name = roleRole.name, id = Some(roleRole.id), permissions = rolesAndPermissions.flatMap(_._2).map {
          permissionRaw =>
            Permission(Actions.withName(permissionRaw.action), permissionRaw.state)
        })
    }

}
