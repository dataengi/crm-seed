package com.dataengi.crm.profiles.repositories

import cats.instances.all._
import com.dataengi.crm.profiles.daos.ProfilesDAO
import com.dataengi.crm.profiles.models.Profile
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}

import scala.concurrent.ExecutionContext

trait ProfilesRepository extends AutoIncRepository[Profile] {

  def findByUserId(userId: Long): Or[Option[Profile]]

  def findProfileByNickName(nickname: String): Or[Option[Profile]]

}

@Singleton
class ProfilesInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[Profile]
    with ProfilesRepository {

  override def beforeSave(id: Long, value: Profile): Profile = value.copy(id = Some(id))

  override def findByUserId(userId: Long): Or[Option[Profile]] = getAll().map(_.find(_.userId == userId))

  override def findProfileByNickName(nickname: String): Or[Option[Profile]] = getAll().map(_.find(_.nickname == nickname))

}

@Singleton
class ProfilesRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext, profilesDAO: ProfilesDAO)
    extends ProfilesRepository {

  override def findByUserId(userId: Long): Or[Option[Profile]] = profilesDAO.findByUserId(userId)

  override def findProfileByNickName(nickname: String): Or[Option[Profile]] = profilesDAO.findByNickname(nickname)

  override def getAll(): Or[List[Profile]] = profilesDAO.all

  override def remove(id: Long): Or[Empty] = profilesDAO.delete(id)

  override def add(value: Profile): Or[Long] = profilesDAO.add(value)

  override def add(values: List[Profile]): Or[List[Long]] = profilesDAO.add(values)

  override def get(id: Long): Or[Profile] = profilesDAO.get(id)

  override def update(id: Long, value: Profile): EmptyOr = profilesDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[Profile]] = profilesDAO.getOption(id)

}
