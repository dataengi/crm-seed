package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.daos.AutoIncBaseDAO
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.errors.UsersDAOErrors
import com.dataengi.crm.identities.models.{Company, Role, User}
import com.dataengi.crm.identities.models.UserStates.UserState
import com.dataengi.crm.identities.slick.tables.identities.UsersTableDescription
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio
import slick.dbio.Effect.{Read, Transactional}
import cats.instances.all._

import scala.concurrent.ExecutionContext

trait UsersDAO extends AutoIncBaseDAO[User] {

  def findByCompany(companyId: Long): Or[List[User]]

  def find(loginInfo: LoginInfo): Or[Option[User]]

  def updateState(userId: Long, state: UserState): EmptyOr

}

@Singleton
class UsersSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                            implicit val executionContext: ExecutionContext)
    extends UsersDAO
    with UsersQueries {

  override def get(key: Long): Or[User] = db.run(selectUser(key)).toOr

  override def getOption(key: Long): Or[Option[User]] = db.run(selectUserOption(key)).toOr

  override def add(obj: User): Or[Long] = db.run(insertUserAction(obj)).toOr

  override def add(values: List[User]): Or[List[Long]] = db.run(insertUsersAction(values)).toOr.map(_.toList)

  override def update(obj: User): Or[Unit] = db.run(updateUserAction(obj)).toEmptyOr

  override def delete(key: Long): Or[Unit] = db.run(deleteCompanyAction(key)).toEmptyOr

  override def all: Or[List[User]] = db.run(UsersAction).toOr.map(_.toList)

  override def findByCompany(companyId: Long): Or[List[User]] = db.run(selectUserByCompanyId(companyId)).toOr.map(_.toList)

  override def find(loginInfo: LoginInfo): Or[Option[User]] = db.run(selectUserByLoginInfo(loginInfo)).toOr

  override def updateState(userId: Long, state: UserState): EmptyOr = db.run(updateUserSate(userId, state)).toEmptyOr

}

trait UsersQueries extends UsersTableDescription with RolesQueries with CompaniesQueries {

  import profile.api._

  val insertUserQuery = Users returning Users.map(_.id)

  def insertUserAction(user: User) =
    (for {
      loginInfo: Long <- insertLoginInfo(user.loginInfo)
      userId: Long    <- Users returning Users.map(_.id) += unMapUser(user, loginInfo)
    } yield userId).transactionally

  def insertUsersAction(users: List[User]) =
    (for {
      loginInfoIds: Seq[Long] <- insertLoginInfo(users.map(_.loginInfo))
      userIds: Seq[Long] <- Users returning Users.map(_.id) ++= users.zip(loginInfoIds).map {
        case (user, loginInfo) => unMapUser(user, loginInfo)
      }
    } yield userIds).transactionally

  def insertLoginInfo(loginInfo: LoginInfo) =
    LoginInfoTableQuery returning LoginInfoTableQuery.map(_.id) += fromLoginInfo(loginInfo)

  def insertLoginInfo(loginInfo: Seq[LoginInfo]) =
    LoginInfoTableQuery returning LoginInfoTableQuery.map(_.id) ++= loginInfo.map(fromLoginInfo)

  def selectLoginInfo(id: Long) =
    for {
      loginInfoOpt: Option[LoginInfoRow] <- LoginInfoTableQuery.filter(_.id === id).result.headOption
      loginInfo: LoginInfoRow            <- loginInfoFromOption(loginInfoOpt)
    } yield loginInfo

  def selectLoginInfoByKey(loginInfo: LoginInfo) =
    LoginInfoTableQuery
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
      .flatMap(optionWithFailed(_, UsersDAOErrors.LoginInfoNotFound))

  def selectLoginInfoByKeyOption(loginInfo: LoginInfo) =
    LoginInfoTableQuery.filter(_.providerKey === loginInfo.providerKey).result.headOption

  def selectLoginInfoId(userId: Long) =
    for {
      userRow: UserRow <- Users
        .filter(_.id === userId)
        .result
        .headOption
        .flatMap(optionWithFailed(_, UsersDAOErrors.LoginInfoNotFound))
    } yield userRow.loginInfoId

  def loginInfoFromOption(loginInfoOpt: Option[LoginInfoRow]) = loginInfoOpt match {
    case Some(value) => DBIO.successful(value)
    case None        => DBIO.failed(UsersDAOErrors.AuthInfoNotFound)
  }

  def updateLoginInfo(loginInfo: LoginInfo) =
    LoginInfoTableQuery.filter(_.providerKey === loginInfo.providerKey).update(fromLoginInfo(loginInfo))

  def selectUser(
      id: Long): slick.dbio.DBIOAction[User, slick.dbio.NoStream, slick.dbio.Effect.Read with slick.dbio.Effect] =
    for {
      userRow: UserRow <- Users
        .filter(_.id === id)
        .result
        .headOption
        .flatMap(optionWithFailed(_, UsersDAOErrors.UserNotFound))
      role: Role              <- selectRole(userRow.roleId).map(_.headOption).flatMap(optionWithFailed(_, UsersDAOErrors.RoleNotFound))
      company: Company        <- selectCompany(userRow.companyId).flatMap(optionWithFailed(_, UsersDAOErrors.CompanyNotFound))
      loginInfo: LoginInfoRow <- selectLoginInfo(userRow.loginInfoId)
    } yield
      mapUser(userRow, role, company, loginInfo)

  def mapUser(userRow: UserRow, role: Role, company: Company, loginInfo: LoginInfoRow): User = {
    User(company = company, loginInfo = toLoginInfo(loginInfo), role = role, state = userRow.state, id = Some(userRow.id))
  }

  def selectUserByLoginInfo(loginInfo: LoginInfo) =
    for {
      loginInfoRow: Option[LoginInfoRow] <- selectLoginInfoByKeyOption(loginInfo)
      userRowOpt: Option[UserRow]        <- extractUserRow(loginInfoRow)
      userOpt: Option[User]              <- userExtract(userRowOpt)
    } yield userOpt

  private def extractUserRow(loginInfoRowOpt: Option[LoginInfoRow]) = loginInfoRowOpt match {
    case Some(loginInfoRow) => Users.filter(_.loginInfoId === loginInfoRow.id).result.headOption
    case None               => DBIO.successful[Option[UserRow]](None)
  }

  def selectUserByCompanyId(companyId: Long) =
    (for {
      usersRow: Seq[UserRow] <- Users.filter(_.companyId === companyId).result
      users                  <- DBIO.sequence(usersRow.map(row => selectUser(row.id)))
    } yield users).transactionally

  def selectUserOption(id: Long) =
    Users.filter(_.id === id).result.headOption.flatMap(userExtract)

  private def optionWithFailed[T](option: Option[T], throwable: Throwable) = option match {
    case Some(value) => DBIO.successful[T](value)
    case None        => DBIO.failed(throwable)
  }

  def updateUserAction(user: User) =
    (for {
      loginInfoId     <- selectLoginInfoId(user.id.get)
      updateUser      <- Users.filter(value => value.id === user.id.get).update(unMapUser(user, loginInfoId))
      updateRole      <- updateRoleAction(user.role.id.get, user.role)
      updateCompany   <- updateCompanyAction(user.company.id.get, user.company)
      updateLoginInfo <- updateLoginInfo(user.loginInfo)
    } yield updateUser).transactionally

  def updateUserSate(userId: Long, state: UserState) = Users.filter(_.id === userId).map(_.state).update(state)

  def deleteUserAction(id: Long) = Users.filter(_.id === id).delete

  def UsersAction = Users.result.flatMap(userRows => DBIO.sequence(userRows.map(userExtract)))

  private def userExtract(
      userRowOpt: Option[UserRow]): slick.dbio.DBIOAction[Option[User], NoStream, Read with Transactional] =
    userRowOpt match {
      case Some(userRow) => userExtract(userRow).map(Option(_))
      case None          => DBIO.successful[Option[User]](None)
    }

  private def userExtract(userRow: UserRow): slick.dbio.DBIOAction[User, dbio.NoStream, Read with Transactional] =
    (for {
      role: Role              <- selectRole(userRow.roleId).map(_.headOption).flatMap(optionWithFailed(_, UsersDAOErrors.RoleNotFound))
      company: Company        <- selectCompany(userRow.companyId).flatMap(optionWithFailed(_, UsersDAOErrors.CompanyNotFound))
      loginInfo: LoginInfoRow <- selectLoginInfo(userRow.loginInfoId)
    } yield User(company = company, loginInfo = toLoginInfo(loginInfo), role = role, state = userRow.state, id = Some(userRow.id))).transactionally

  def unMapUser(user: User, loginInfoId: Long) =
    UserRow(
      loginInfoId = loginInfoId,
      roleId = user.role.id.get,
      companyId = user.company.id.get,
      state = user.state,
      id = user.id.getOrElse(0)
    )

}
