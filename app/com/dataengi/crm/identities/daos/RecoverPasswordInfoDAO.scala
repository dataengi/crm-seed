package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.daos.AutoIncBaseDAO
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.errors.RecoverPasswordInfoDAOErrors
import com.dataengi.crm.identities.models.RecoverPasswordInfo
import com.dataengi.crm.identities.slick.tables.identities.RecoverPasswordInfoTableDescription
import com.google.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import cats.instances.all._

import scala.concurrent.ExecutionContext

trait RecoverPasswordInfoDAO extends AutoIncBaseDAO[RecoverPasswordInfo] {

  def getByRecoverId(recoverId: String): Or[Option[RecoverPasswordInfo]]

}

@Singleton
class RecoverPasswordInfoSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                                          implicit val executionContext: ExecutionContext)
    extends RecoverPasswordInfoDAO
    with RecoverPasswordInfoQueries {

  override def get(key: Long): Or[RecoverPasswordInfo] =
    db.run(selectRecoverPasswordInfo(key)).toOrWithLeft(RecoverPasswordInfoDAOErrors.RecoverPasswordInfoNotFound)

  override def getOption(key: Long): Or[Option[RecoverPasswordInfo]] = db.run(selectRecoverPasswordInfo(key)).toOr

  override def add(obj: RecoverPasswordInfo): Or[Long] = db.run(insertRecoverPasswordInfoAction(obj)).toOr

  override def add(values: List[RecoverPasswordInfo]): Or[List[Long]] =
    db.run(insertRecoverPasswordInfoAction(values)).toOr.map(_.toList)

  override def update(obj: RecoverPasswordInfo): Or[Unit] =
    db.run(updateRecoverPasswordInfoAction(obj.id.get, obj)).toEmptyOr

  override def delete(key: Long): Or[Unit] = db.run(deleteRecoverPasswordInfoAction(key)).toEmptyOr

  override def all: Or[List[RecoverPasswordInfo]] = db.run(recoverPasswordInfoAction).toOr.map(_.toList)

  override def getByRecoverId(recoverId: String) =
    db.run(selectRecoverPasswordInfoByRecoverId(recoverId)).toOr
}

trait RecoverPasswordInfoQueries extends RecoverPasswordInfoTableDescription {

  import profile.api._

  val insertRecoverPasswordInfoQuery = RecoverPasswordInfoTableQuery returning RecoverPasswordInfoTableQuery.map(_.id)

  def insertRecoverPasswordInfoAction(recoverPasswordInfo: RecoverPasswordInfo) =
    insertRecoverPasswordInfoQuery += recoverPasswordInfo

  def insertRecoverPasswordInfoAction(recoverPasswordInfo: List[RecoverPasswordInfo]) =
    insertRecoverPasswordInfoQuery ++= recoverPasswordInfo

  def updateRecoverPasswordInfoAction(id: Long, recoverPasswordInfo: RecoverPasswordInfo) =
    RecoverPasswordInfoTableQuery.filter(_.id === id).update(recoverPasswordInfo)

  def deleteRecoverPasswordInfoAction(id: Long) = RecoverPasswordInfoTableQuery.filter(_.id === id).delete

  def selectRecoverPasswordInfo(id: Long) = RecoverPasswordInfoTableQuery.filter(_.id === id).result.headOption

  def selectRecoverPasswordInfoByRecoverId(recoverId: String) =
    RecoverPasswordInfoTableQuery.filter(_.recoverId === recoverId).result.headOption

  def recoverPasswordInfoAction = RecoverPasswordInfoTableQuery result

}
