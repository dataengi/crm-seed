package com.dataengi.crm.identities.services

import com.dataengi.crm.identities.models.{IdentityInfo, User, UserStates}
import com.dataengi.crm.common.context.types._
import com.google.inject.{Inject, Singleton}
import cats.instances.all._

import scala.concurrent.ExecutionContext

trait UsersManagementService {

  def getCompanyMembers(companyId: Long, identity: User): Or[List[User]]

  def identityInfo(user: User): Or[IdentityInfo]

  def activateUser(userId: Long): EmptyOr

  def deactivateUser(userId: Long): EmptyOr

}

@Singleton
class UsersManagementServiceImplementation @Inject()(userService: UsersService,
                                                     rolesService: RolesService,
                                                     implicit val executionContext: ExecutionContext)
    extends UsersManagementService {

  // TODO: Verify permissions
  override def getCompanyMembers(companyId: Long, identity: User): Or[List[User]] = userService.getCompanyMembers(companyId)

  override def identityInfo(user: User): Or[IdentityInfo] =
    for {
      companyMembers <- userService.getCompanyMembers(user.company.id.get).map(_.map(_.id.get))
      roles          <- rolesService.allRoles()
    } yield IdentityInfo(user, companyMembers, roles)

  override def activateUser(userId: Long): EmptyOr =
    for {
      user        <- userService.get(userId)
      updateState <- userService.updateState(userId, state = UserStates.Activated)
    } yield updateState

  override def deactivateUser(userId: Long): EmptyOr =
    for {
      user        <- userService.get(userId)
      updateState <- userService.updateState(userId, state = UserStates.Deactivated)
    } yield updateState

}
