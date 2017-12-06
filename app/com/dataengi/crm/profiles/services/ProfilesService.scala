package com.dataengi.crm.profiles.services

import cats.instances.all._
import com.dataengi.crm.profiles.controllers.data.{GetUsersProfilesData, UpdateProfileData}
import com.dataengi.crm.profiles.models.Profile
import com.dataengi.crm.profiles.repositories.ProfilesRepository
import com.dataengi.crm.profiles.services.errors.ProfileServiceErrors
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.models.User
import com.dataengi.crm.identities.services.UsersService
import scalty.types.or
import cats.syntax.traverse._

import scala.concurrent.ExecutionContext

trait ProfilesService {

  def get(body: GetUsersProfilesData): Or[List[Profile]]

  def get(user: User): Or[Profile]

  def getOption(userId: Long): Or[Option[Profile]]

  def update(updateProfileData: UpdateProfileData, user: User): EmptyOr

  def existProfileWithNickname(nickname: String): EmptyOr

}

@Singleton
class ProfilesServiceImplementation @Inject()(profilesRepository: ProfilesRepository,
                                              avatarService: AvatarService,
                                              usersService: UsersService,
                                              implicit val executionContext: ExecutionContext)
    extends ProfilesService {

  override def getOption(userId: Long) = profilesRepository.findByUserId(userId)

  override def update(data: UpdateProfileData, user: User): EmptyOr =
    for {
      profile             <- get(user)
      nicknameCheckResult <- existProfileWithNickname(data.nickname, profile.nickname)
      updateResult <- profilesRepository.update(
        profile.id.get,
        profile
          .copy(nickname = data.nickname, firstName = data.firstName, lastName = data.lastName, avatarUrl = data.avatarUrl))
    } yield updateResult

  private def existProfileWithNickname(newNickname: String, currentNickname: String): EmptyOr =
    if (newNickname != currentNickname) {
      existProfileWithNickname(newNickname)
    } else {
      currentNickname.toEmptyOr
    }

  private def checkNickname(profileOpt: Option[Profile]): EmptyOr = profileOpt match {
    case Some(profile) => ProfileServiceErrors.profileWithNicknameAlreadyExist(profile.nickname).toErrorOr
    case None          => or.EMPTY_OR
  }

  override def get(user: User): Or[Profile] = getOption(user.id.get).flatMap {
    case Some(profile) => profile.toOr
    case None          => createForUser(user)
  }

  private def createForUser(user: User): Or[Profile] =
    for {
      avatarUrlOpt <- avatarService.retrieveURL(user.loginInfo.providerKey)
      profile = Profile(userId = user.id.get,
                        nickname = user.loginInfo.providerKey,
                        email = user.loginInfo.providerKey,
                        avatarUrl = avatarUrlOpt)
      profileId <- profilesRepository.add(profile)
    } yield profile.copy(id = Some(profileId))

  override def existProfileWithNickname(nickname: String): EmptyOr =
    profilesRepository.findProfileByNickName(nickname).flatMap(checkNickname)

  override def get(data: GetUsersProfilesData): Or[List[Profile]] =
    for {
      profiles <- data.userIds.traverse[Or, Profile](getUserProfile)
    } yield profiles

  private def getUserProfile(userId: Long) = getOption(userId).flatMap {
    case Some(profile) => profile.toOr
    case None          => getDefaultUserProfile(userId)
  }

  private def getDefaultUserProfile(userId: Long) = {
    for {
      user    <- usersService.get(userId)
      profile <- createForUser(user)
    } yield profile
  }

}
