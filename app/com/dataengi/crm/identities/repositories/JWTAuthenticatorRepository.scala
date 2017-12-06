package com.dataengi.crm.identities.repositories

import com.dataengi.crm.identities.daos.JWTAuthenticatorDAO
import com.dataengi.crm.identities.models.JWTAuthenticatorData
import com.google.common.cache.CacheBuilder
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.crypto.AuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorSettings}

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scalacache.guava.GuavaCache

trait JWTAuthenticatorRepository extends AuthenticatorRepository[JWTAuthenticator]

@Singleton
class JWTAuthenticatorRepositoryInMemoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends JWTAuthenticatorRepository {

  protected val repository = TrieMap[String, JWTAuthenticator]()

  override def find(id: String): Future[Option[JWTAuthenticator]] = Future {
    repository.get(id)
  }

  override def add(authenticator: JWTAuthenticator): Future[JWTAuthenticator] = Future {
    repository.put(authenticator.id, authenticator)
    authenticator
  }

  override def update(authenticator: JWTAuthenticator): Future[JWTAuthenticator] = Future {
    repository.update(authenticator.id, authenticator)
    authenticator
  }

  override def remove(id: String): Future[Unit] = Future {
    repository.remove(id)
  }
}

@Singleton
class JWTAuthenticatorSerializableRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext,
                                                                     authenticatorDAO: JWTAuthenticatorDAO,
                                                                     authenticatorEncoder: AuthenticatorEncoder,
                                                                     conf: JWTAuthenticatorSettings)
    extends JWTAuthenticatorRepository {

  override def find(id: String): Future[Option[JWTAuthenticator]] =
    authenticatorDAO
      .getOption(id)
      .map(_.map(data => JWTAuthenticator.unserialize(data.authenticator, authenticatorEncoder, conf).get))

  override def add(authenticator: JWTAuthenticator): Future[JWTAuthenticator] =
    for {
      data      <- serializeData(authenticator)
      addResult <- authenticatorDAO.add(data)
    } yield authenticator

  private def serializeData(authenticator: JWTAuthenticator): Future[JWTAuthenticatorData] = {
    Future {
      val serializedData = JWTAuthenticator.serialize(authenticator, authenticatorEncoder, conf)
      JWTAuthenticatorData(serializedData, authenticator.id)
    }
  }

  override def update(authenticator: JWTAuthenticator): Future[JWTAuthenticator] =
    for {
      updatedAuthenticator <- authenticatorDAO.get(authenticator.id)
      data                 <- serializeData(authenticator)
      updateResult         <- authenticatorDAO.update(data.copy(id = updatedAuthenticator.id))
    } yield authenticator

  override def remove(id: String): Future[Unit] = authenticatorDAO.delete(id)

}

@Singleton
class JWTAuthenticatorCacheRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends JWTAuthenticatorRepository {

  import scalacache._

  val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Object]
  implicit val scalaCache  = ScalaCache(GuavaCache(underlyingGuavaCache))
  val cache                = typed[JWTAuthenticator, NoSerialization]

  override def find(id: String): Future[Option[JWTAuthenticator]] = cache.get(id)

  override def add(authenticator: JWTAuthenticator): Future[JWTAuthenticator] =
    cache.put(authenticator.id)(authenticator).map(_ => authenticator)

  override def update(authenticator: JWTAuthenticator): Future[JWTAuthenticator] =
    cache.put(authenticator.id)(authenticator).map(_ => authenticator)

  override def remove(id: String): Future[Unit] = cache.remove(id)

}
