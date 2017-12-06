package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.daos.ContaineredBaseDAO
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.models.JWTAuthenticatorData
import com.dataengi.crm.identities.slick.tables.identities.JWTAuthenticatorTableDescription
import com.google.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import scalty.types.empty

import scala.concurrent.{ExecutionContext, Future}

trait JWTAuthenticatorDAO extends ContaineredBaseDAO[String, JWTAuthenticatorData, Future]

@Singleton
class JWTAuthenticatorSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                                       implicit val executionContext: ExecutionContext)
    extends JWTAuthenticatorDAO
    with JWTAuthenticatorQueries {

  override def get(key: String): Future[JWTAuthenticatorData] = db.run(selectJWTAuthenticator(key)).map(_.get)

  override def getOption(key: String): Future[Option[JWTAuthenticatorData]] = db.run(selectJWTAuthenticator(key))

  override def add(obj: JWTAuthenticatorData): Future[String] = db.run(insertJWTAuthenticator(obj))

  override def add(values: List[JWTAuthenticatorData]): Future[List[String]] = db.run(insertJWTAuthenticators(values)).map(_.toList)

  override def update(obj: JWTAuthenticatorData): Future[Empty] = db.run(updateJWTAuthenticator(obj)).map(_ => empty.EMPTY_INSTANCE)

  override def delete(key: String): Future[Empty] = db.run(deleteJWTAuthenticator(key)).map(_ => empty.EMPTY_INSTANCE)

  override def all: Future[List[JWTAuthenticatorData]] = db.run(JWTAuthenticatorsAction).map(_.toList)

}

trait JWTAuthenticatorQueries extends JWTAuthenticatorTableDescription {

  import profile.api._

  val insertAuthenticator = JWTAuthenticators returning JWTAuthenticators.map(_.id) into((authenticator, id) => authenticator.identifier)

  def insertJWTAuthenticator(authenticator: JWTAuthenticatorData) = insertAuthenticator += authenticator

  def insertJWTAuthenticators(authenticators: List[JWTAuthenticatorData]) = insertAuthenticator ++= authenticators

  def selectJWTAuthenticator(id: String) = JWTAuthenticators.filter(_.identifier === id).result.headOption

  def deleteJWTAuthenticator(id: String) = JWTAuthenticators.filter(_.identifier === id).delete

  def updateJWTAuthenticator(authenticator: JWTAuthenticatorData) =
    JWTAuthenticators.filter(_.identifier === authenticator.identifier).update(authenticator)

  def JWTAuthenticatorsAction = JWTAuthenticators.result

}
