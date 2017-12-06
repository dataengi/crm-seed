package com.dataengi.crm.identities.repositories

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.ExecutionContext
import cats.instances.all._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.UsersDAO
import com.dataengi.crm.identities.models.User
import com.dataengi.crm.identities.models.UserStates.UserState

trait UsersRepository extends AutoIncRepository[User] {

  def findByCompany(companyId: Long): Or[List[User]]

  def find(loginInfo: LoginInfo): Or[Option[User]]

  def updateState(id: Long, state: UserState): EmptyOr

}

@Singleton
class UsersInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[User]
    with UsersRepository {

  override protected def beforeSave(key: Long, value: User): User = value.copy(id = Some(key))

  override def find(loginInfo: LoginInfo): Or[Option[User]] = repository.values.find(_.loginInfo == loginInfo).toOr

  override def findByCompany(companyId: Long): Or[List[User]] = getAll().map(_.filter(_.company.id.exists(_ == companyId)))

  override def updateState(id: Long, state: UserState): EmptyOr =
    getAll().map(_.find(_.id == id).map(user => update(id, user.copy(state = state)))).toEmptyOr
}

@Singleton
class UsersRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext, usersDAO: UsersDAO)
    extends UsersRepository {

  override def findByCompany(companyId: Long): Or[List[User]] = usersDAO.findByCompany(companyId)

  override def find(loginInfo: LoginInfo): Or[Option[User]] = usersDAO.find(loginInfo)

  override def getAll(): Or[List[User]] = usersDAO.all

  override def remove(id: Long): Or[Empty] = usersDAO.delete(id)

  override def add(value: User): Or[Long] = usersDAO.add(value)

  override def add(values: List[User]): Or[List[Long]] = usersDAO.add(values)

  override def get(id: Long): Or[User] = usersDAO.get(id)

  override def update(id: Long, value: User): EmptyOr = usersDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[User]] = usersDAO.getOption(id)

  override def updateState(id: Long, state: UserState): EmptyOr = usersDAO.updateState(id, state)

}
