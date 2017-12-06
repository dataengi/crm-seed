package com.dataengi.crm.identities.repositories

import com.google.inject.{Inject, Singleton}
import cats.instances.all._
import com.dataengi.crm.common.repositories.{BaseInMemoryRepository, Repository}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.RecoverPasswordInfoDAO
import com.dataengi.crm.identities.models.RecoverPasswordInfo

import scala.concurrent.ExecutionContext

trait RecoverPasswordInfoRepository extends Repository[RecoverPasswordInfo] {
  override type Key = Long

  def getByRecoverId(recoverId: String): Or[Option[RecoverPasswordInfo]]

}

@Singleton
class RecoverPasswordInfoRepositoryInMemoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[RecoverPasswordInfo]
    with RecoverPasswordInfoRepository {

  override protected def beforeSave(key: Long, value: RecoverPasswordInfo): RecoverPasswordInfo = value.copy(id = Some(key))

  override def getByRecoverId(recoverId: String): Or[Option[RecoverPasswordInfo]] =
    getAll().map(_.find(_.recoverId == recoverId))

}

@Singleton
class RecoverPasswordInfoRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext,
                                                            recoverPasswordInfoDAO: RecoverPasswordInfoDAO)
    extends RecoverPasswordInfoRepository {

  override def getByRecoverId(recoverId: String): Or[Option[RecoverPasswordInfo]] =
    recoverPasswordInfoDAO.getByRecoverId(recoverId)

  override def getAll(): Or[List[RecoverPasswordInfo]] = recoverPasswordInfoDAO.all

  override def remove(id: Long): Or[Empty] = recoverPasswordInfoDAO.delete(id)

  override def add(value: RecoverPasswordInfo): Or[Long] = recoverPasswordInfoDAO.add(value)

  override def add(values: List[RecoverPasswordInfo]): Or[List[Long]] = recoverPasswordInfoDAO.add(values)

  override def get(id: Long): Or[RecoverPasswordInfo] = recoverPasswordInfoDAO.get(id)

  override def update(id: Long, value: RecoverPasswordInfo): EmptyOr =
    recoverPasswordInfoDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[RecoverPasswordInfo]] = recoverPasswordInfoDAO.getOption(id)

}
