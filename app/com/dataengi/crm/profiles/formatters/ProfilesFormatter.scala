package com.dataengi.crm.profiles.formatters

import com.dataengi.crm.profiles.controllers.data.{CreateProfileData, GetUsersProfilesData, UpdateProfileData}
import com.dataengi.crm.profiles.models.Profile
import play.api.libs.json.{JsValue, Json, OFormat}

trait ProfilesFormatter {

  implicit val profileFormatter: OFormat[Profile]                           = Json.format[Profile]
  implicit val createProfileFormatter: OFormat[CreateProfileData]           = Json.format[CreateProfileData]
  implicit val updateProfileFormatter: OFormat[UpdateProfileData]           = Json.format[UpdateProfileData]
  implicit val getUsersProfilesDataFormatter: OFormat[GetUsersProfilesData] = Json.format[GetUsersProfilesData]

}

object ProfilesFormatter extends ProfilesFormatter
