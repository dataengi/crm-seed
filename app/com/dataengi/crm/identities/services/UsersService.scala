package com.dataengi.crm.identities.services

import com.dataengi.crm.identities.models.User
import com.dataengi.crm.common.context.types._
import cats.syntax.either._
import com.dataengi.crm.identities.models.UserStates.UserState
import com.dataengi.crm.identities.repositories.{CompaniesRepository, RolesRepository, UsersRepository}
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.{ExecutionContext, Future}

trait UsersService extends IdentityService[User] {

  def getCompanyMembers(companyId: Long): Or[List[User]]

  def save(user: User): Or[Long]

  def get(id: Long): Or[User]

  def getOption(id: Long): Or[Option[User]]

  def all(): Or[List[User]]

  def update(id: Long, user: User): EmptyOr

  def findByEmail(email: String): Or[Option[User]]

  def updateState(userId: Long, state: UserState): EmptyOr

}

@Singleton
class UsersServiceImplementation @Inject()(usersRepository: UsersRepository,
                                           companiesRepository: CompaniesRepository,
                                           rolesRepository: RolesRepository,
                                           implicit val executionContext: ExecutionContext)
    extends UsersService {

  override def save(user: User): Or[Long] = usersRepository.add(user) // TODO: check company, role, replace on UserData

  override def get(id: Long): Or[User] = usersRepository.get(id)

  override def getOption(id: Long): Or[Option[User]] = usersRepository.get(id).value.map(_.toOption).toOr

  override def all(): Or[List[User]] = usersRepository.getAll()

  override def update(id: Long, user: User): EmptyOr = usersRepository.update(id, user)

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    usersRepository.find(loginInfo).value.map(_.getOrElse(None))

  override def findByEmail(email: String): Or[Option[User]] = usersRepository.find(LoginInfo(CredentialsProvider.ID, email))

  override def getCompanyMembers(companyId: Long): Or[List[User]] = usersRepository.findByCompany(companyId)

  override def updateState(userId: Long, state: UserState): EmptyOr = usersRepository.updateState(userId, state)

}
