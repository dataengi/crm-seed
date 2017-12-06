package com.dataengi.crm.profiles.arbitraries

import com.dataengi.crm.profiles.controllers.data.UpdateProfileData
import com.dataengi.crm.profiles.models.Profile
import com.dataengi.crm.common.arbitraries.CommonArbitrary
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by nk91 on 16.03.17.
  */
trait ProfilesArbitrary extends CommonArbitrary {

  implicit val profileUpdateDataArbitrary: Arbitrary[UpdateProfileData] = Arbitrary(Gen.resultOf(UpdateProfileData))
  implicit val profileArbitrary: Arbitrary[Profile]                     = Arbitrary(Gen.resultOf(Profile))

}
