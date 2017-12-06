package com.dataengi.crm.profiles.daos

import cats.instances.all._
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.daos.AutoIncBaseDAO
import play.api.db.slick.DatabaseConfigProvider
import com.dataengi.crm.profiles.daos.errors.ProfilesDAOErrors.ProfileNotFound
import com.dataengi.crm.profiles.models.Profile
import com.dataengi.crm.profiles.slick.tables.ProfilesTableDescription

import scala.concurrent.ExecutionContext

trait ProfilesDAO extends AutoIncBaseDAO[Profile] {

  def findByUserId(userId: Long): Or[Option[Profile]]

  def findByNickname(nickname: String): Or[Option[Profile]]

  def clear: EmptyOr

}

@Singleton
class ProfilesSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                               implicit val executionContext: ExecutionContext)
    extends ProfilesDAO
    with ProfilesDAOQueries {

  override def get(id: Long): Or[Profile] = db.run(selectProfileById(id)).toOrWithLeft(ProfileNotFound)

  override def getOption(id: Long): Or[Option[Profile]] = db.run(selectProfileById(id)).toOr

  override def add(profile: Profile): Or[Long] = db.run(insertProfile(profile)).toOr

  override def add(values: List[Profile]): Or[List[Long]] = db.run(insertProfiles(values)).toOr.map(_.toList)

  override def update(obj: Profile): Or[Unit] = db.run(updateProfile(obj)).toEmptyOr

  override def delete(id: Long): Or[Unit] = db.run(deleteProfileById(id)).toEmptyOr

  override def all: Or[List[Profile]] = db.run(selectAllProfiles).toOr.map(_.toList)

  override def findByUserId(userId: Long): Or[Option[Profile]] = db.run(selectProfileByUserId(userId)).toOr

  override def findByNickname(nickname: String): Or[Option[Profile]] = db.run(selectProfileByNickname(nickname)).toOr

  override def clear: EmptyOr = db.run(deleteAll).toEmptyOr
}

trait ProfilesDAOQueries extends ProfilesTableDescription {

  import profile.api._

  def selectAllProfiles = Profiles.result.map(_.map(mapProfile))

  def selectProfileById(profileId: Long) = Profiles.filter(_.id === profileId).result.headOption.map(_.map(mapProfile))

  def selectProfileByUserId(userId: Long) = Profiles.filter(_.userId === userId).result.headOption.map(_.map(mapProfile))

  def selectProfileByNickname(nickname: String) =
    Profiles.filter(_.nickname === nickname).result.headOption.map(_.map(mapProfile))

  def insertProfile(profile: Profile) = Profiles returning Profiles.map(_.id) += unMapProfile(profile)

  def insertProfiles(profiles: List[Profile]) = Profiles returning Profiles.map(_.id) ++= profiles.map(unMapProfile)

  def updateProfile(profile: Profile) = Profiles.filter(_.id === profile.id.get).update(unMapProfile(profile))

  def deleteProfileById(profileId: Long) = Profiles.filter(_.id === profileId).delete

  def deleteAll = Profiles.delete

  def mapProfile(profileRow: ProfileRow): Profile = Profile(
    userId = profileRow.userId,
    nickname = profileRow.nickname,
    email = profileRow.email,
    firstName = profileRow.firstName,
    lastName = profileRow.lastName,
    avatarUrl = profileRow.avatarUrl,
    id = Some(profileRow.id)
  )

  def unMapProfile(profile: Profile): ProfileRow = ProfileRow(
    userId = profile.userId,
    nickname = profile.nickname,
    email = profile.email,
    firstName = profile.firstName,
    lastName = profile.lastName,
    avatarUrl = profile.avatarUrl,
    id = profile.id.getOrElse(0)
  )

}
