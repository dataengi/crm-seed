package com.dataengi.crm.profiles.daos

import play.api.test.PlaySpecification
import com.dataengi.crm.profiles.context.ProfilesContext
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._

class ProfilesDAOSpec extends PlaySpecification with ProfilesContext {

  "ProfilesDAOSpecification" should {

    "add profile" in {
      val profile   = profileArbitrary.arbitrary.sample.get
      val addResult = profilesDAO.add(profile).await()
      addResult.isRight === true
    }

    "get profile by id" in {
      val profile   = profileArbitrary.arbitrary.sample.get
      val addResult = profilesDAO.add(profile).await()
      addResult.isRight === true

      val getProfileResult = profilesDAO.get(addResult.value).await()
      getProfileResult.isRight === true
    }

    "get profile by nickname" in {
      val profile   = profileArbitrary.arbitrary.sample.get
      val addResult = profilesDAO.add(profile).await()
      addResult.isRight === true

      val getProfileByNickname = profilesDAO.findByNickname(profile.nickname).await()
      getProfileByNickname.isRight === true
      getProfileByNickname.value.get.copy(id = None) === profile.copy(id = None)
    }

    "get profile by userId" in {
      val result = profilesDAO.clear.await()
      result.isRight === true
      val profileData = profileArbitrary.arbitrary.sample.get.copy(id = None)
      val addResult   = profilesDAO.add(profileData).await()
      addResult.isRight === true
      val getProfileByIdResult = profilesDAO.get(addResult.value).await()
      getProfileByIdResult.isRight === true
      val profile           = getProfileByIdResult.value
      val findProfileResult = profilesDAO.findByUserId(profile.userId).await()
      findProfileResult.isRight === true
      findProfileResult.value.get === profile
    }

  }

}
