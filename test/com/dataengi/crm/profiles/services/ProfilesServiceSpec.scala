package com.dataengi.crm.profiles.services

import com.dataengi.crm.profiles.models.Profile
import play.api.test.PlaySpecification
import com.dataengi.crm.profiles.context.ProfilesContext
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import org.specs2.runner.SpecificationsFinder

/**
  * Created by nk91 on 31.01.17.
  */
class ProfilesServiceSpec extends PlaySpecification with ProfilesContext with SpecificationsFinder {

  sequential

  "ProfilesServiceSpecification" should {

    "check exist nickname" in {
      val getRootProfileResult = profilesService.get(rootUser).await()
      getRootProfileResult.isRight === true
      val rootProfile: Profile = getRootProfileResult.value
      val checkRootNickNameResult = profilesService.existProfileWithNickname(rootProfile.nickname).await()
      checkRootNickNameResult.isLeft === true
      val checkNonExistNicknameResult = profilesService.existProfileWithNickname("NonExistUserNickname").await()
      checkNonExistNicknameResult.isRight === true
    }

    "get avatar for root user" in {
      val avatarResult = avatarsService.retrieveURL(rootUser.loginInfo.providerKey).await()
      avatarResult.isRight === true
      avatarResult.value.isDefined === true
    }

    "get profile for root user" in {
      val getProfileResult = profilesService.get(rootUser).await()
      getProfileResult.isRight === true
    }

    "update profile of root user" in {
      val getRootProfileResult = profilesService.get(rootUser).await()
      getRootProfileResult.isRight === true
      val rootProfile: Profile = getRootProfileResult.value
      val updateData   = profileUpdateDataArbitrary.arbitrary.sample.get
      val updateResult = profilesService.update(updateData, rootUser).await()
      updateResult.isRight === true
      val getProfileResult = profilesService.get(rootUser).await()
      getProfileResult.isRight === true
      getProfileResult.value.nickname === updateData.nickname
      getProfileResult.value.email === rootProfile.email
      getProfileResult.value.firstName === updateData.firstName
      getProfileResult.value.lastName === updateData.lastName
      getProfileResult.value.avatarUrl === updateData.avatarUrl
    }

  }

}
