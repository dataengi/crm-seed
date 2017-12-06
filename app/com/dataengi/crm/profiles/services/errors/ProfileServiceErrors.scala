package com.dataengi.crm.profiles.services.errors

import com.dataengi.crm.common.context.types.AppErrorResult

object ProfileServiceErrors {

  def profileWithNicknameAlreadyExist(nickname: String) =
    ProfileServiceError(s"Profile with nickname=$nickname already exist. Choose another nickname")

  def avatarReceivingError(code: Int, url: String) =
    AvatarServiceError(s"Couldn't get avatar. Error code=$code for url=$url")

  def avatarCreatingUrlError(email: String) =
    AvatarServiceError(s"Couldn't create avatar's url for email=$email")

}

case class ProfileServiceError(description: String) extends AppErrorResult
case class AvatarServiceError(description: String) extends AppErrorResult
