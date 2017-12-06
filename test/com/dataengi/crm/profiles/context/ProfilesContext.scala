package com.dataengi.crm.profiles.context

import com.dataengi.crm.profiles.controllers.data.UpdateProfileData
import com.dataengi.crm.profiles.daos.ProfilesDAO
import com.dataengi.crm.profiles.models.Profile
import com.dataengi.crm.profiles.services.{AvatarService, ProfilesService}
import com.dataengi.crm.identities.context.UsersContext
import org.scalacheck.{Arbitrary, Gen}
import com.dataengi.crm.profiles.services.ProfilesService

/**
  * Created by nk91 on 31.01.17.
  */
trait ProfilesContext extends UsersContext {

  implicit val stringGen: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  implicit val profileUpdateDataArbitrary: Arbitrary[UpdateProfileData] = Arbitrary(Gen.resultOf(UpdateProfileData))
  implicit val profileArbitrary: Arbitrary[Profile]                     = Arbitrary(Gen.resultOf(Profile))

  lazy val profilesService = application.injector.instanceOf[ProfilesService]
  lazy val avatarsService  = application.injector.instanceOf[AvatarService]
  lazy val profilesDAO     = application.injector.instanceOf[ProfilesDAO]

}
