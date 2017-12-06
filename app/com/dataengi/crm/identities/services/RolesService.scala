package com.dataengi.crm.identities.services

import com.dataengi.crm.identities.repositories.RolesRepository
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.actions.ActionsProvider
import com.dataengi.crm.identities.errors.RolesServiceErrors
import com.dataengi.crm.identities.models.{Role, User}
import com.google.inject.{Inject, Singleton}
import scalty.types.or
import cats.instances.all._

import scala.concurrent.ExecutionContext

trait RolesService {

  def allRoles(identity: User): Or[List[Role]]

  def add(role: Role): Or[Long]

  def add(roles: List[Role]): Or[List[Long]]

  def get(id: Long): Or[Role]

  def update(id: Long, role: Role): EmptyOr

  def findOption(name: String): Or[Option[Role]]

  def find(name: String): Or[Role]

  def allRoles(): Or[List[Role]]

  def allRolesExceptAdvertiser(): Or[List[Role]]

  def clearAllPermissions(): EmptyOr

}

@Singleton
class RolesServiceImplementation @Inject()(rolesRepository: RolesRepository,
                                           actionsProvider: ActionsProvider,
                                           implicit val executionContext: ExecutionContext)
    extends RolesService {

  override def add(role: Role): Or[Long] =
    for {
      actionsResult        <- role.permissions.traverseC(permission => actionsProvider.find(permission.action.toString))
      checkExistRoleResult <- findOption(role.name).flatMap(checkExistRole)
      addRoleResult        <- rolesRepository.add(role)
    } yield addRoleResult

  private def checkExistRole(roleOption: Option[Role]): EmptyOr = roleOption match {
    case Some(role) => RolesServiceErrors.RolesAlreadyExist.toErrorOrWithType[Empty]
    case None       => or.EMPTY_OR
  }

  override def add(roles: List[Role]): Or[List[Long]] = rolesRepository.add(roles)

  override def get(id: Long): Or[Role] = rolesRepository.get(id)

  override def update(id: Long, role: Role): EmptyOr = rolesRepository.update(id, role)

  override def findOption(name: String): Or[Option[Role]] = rolesRepository.find(name)

  override def allRoles(): Or[List[Role]] = rolesRepository.getAll()

  override def allRolesExceptAdvertiser(): Or[List[Role]] =
    rolesRepository.getAll().map(_.filterNot(_.name == RolesConfiguration.Advertiser.name))

  override def find(name: String): Or[Role] = findOption(name).toOrWithLeft(RolesServiceErrors.roleWithNameNotFound(name))

  override def allRoles(identity: User): Or[List[Role]] = identity.role.name match {
    case RolesConfiguration.Root => allRolesExceptAdvertiser()
    case RolesConfiguration.CompanyManager.name =>
      allRolesExceptAdvertiser().map(_.filterNot(_.name == RolesConfiguration.Root))
    case RolesConfiguration.Manager.name =>
      allRolesExceptAdvertiser().map(_.filterNot(role => RolesConfiguration.ManagerLimitRoles.contains(role.name)))
    case othersRoles => RolesServiceErrors.ReadingRolesPermissionDenied.toErrorOrWithType[List[Role]]
  }

  override def clearAllPermissions(): EmptyOr = rolesRepository.clearAllPermissions()
}
