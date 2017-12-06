package com.dataengi.crm.identities.daos

import com.dataengi.crm.identities.slick.queries.Queries
import com.dataengi.crm.identities.slick.tables.credentionals.{LoginInfoTableDescription, PasswordInfoTableDescription}
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PasswordInfoDAOSlickImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                                   implicit val executionContext: ExecutionContext)
    extends PasswordInfoDAO
    with PasswordInfoTableDescription
    with LoginInfoTableDescription
    with PasswordInfoDAOQueries {

  import profile.api._

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    db.run(passwordInfoQuery(loginInfo).result.headOption).map { rawPasswordInfoOption =>
      rawPasswordInfoOption.map(rawPasswordInfo =>
        PasswordInfo(rawPasswordInfo.hasher, rawPasswordInfo.password, rawPasswordInfo.salt))
    }
  }

  /**
    * Adds new auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be added.
    * @param authInfo The auth info to add.
    * @return The added auth info.
    */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
    * Updates the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be updated.
    * @param authInfo The auth info to update.
    * @return The updated auth info.
    */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
    * Saves the auth info for the given login info.
    *
    * This method either adds the auth info if it doesn't exists or it updates the auth info
    * if it already exists.
    *
    * @param loginInfo The login info for which the auth info should be saved.
    * @param authInfo The auth info to save.
    * @return The saved auth info.
    */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = loginInfoQuery(loginInfo).joinLeft(PasswordInfoTableQuery).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (rawLoginInfo, Some(rawPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (rawLoginInfo, None)                  => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  /**
    * Removes the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be removed.
    * @return A future to wait for the process to be completed.
    */
  def remove(loginInfo: LoginInfo): Future[Unit] =
    db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())
}

trait PasswordInfoDAOQueries extends Queries with LoginInfoTableDescription with PasswordInfoTableDescription {
  implicit val executionContext: ExecutionContext

  import profile.api._

  def loginInfoQuery(loginInfo: LoginInfo) = {
    LoginInfoTableQuery.filter { rawLoginInfo =>
      val providerId  = rawLoginInfo.providerID === loginInfo.providerID
      val providerKey = rawLoginInfo.providerKey === loginInfo.providerKey
      providerId && providerKey
    }
  }

  protected def passwordInfoQuery(loginInfo: LoginInfo) =
    for {
      rawLoginInfo    <- loginInfoQuery(loginInfo)
      rawPasswordInfo <- PasswordInfoTableQuery if rawPasswordInfo.loginInfoId === rawLoginInfo.id
    } yield rawPasswordInfo

  // Use subquery workaround instead of join to get authinfo because slick only supports selecting
  // from a single table for update/delete queries (https://github.com/slick/slick/issues/684).
  protected def passwordInfoSubQuery(loginInfo: LoginInfo) =
    PasswordInfoTableQuery.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    loginInfoQuery(loginInfo).result.head.flatMap { rawLoginInfo =>
      PasswordInfoTableQuery +=
        PasswordInfoRaw(authInfo.hasher, authInfo.password, authInfo.salt, rawLoginInfo.id)
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    passwordInfoSubQuery(loginInfo)
      .map(rawPasswordInfo => (rawPasswordInfo.hasher, rawPasswordInfo.password, rawPasswordInfo.salt))
      .update((authInfo.hasher, authInfo.password, authInfo.salt))

}