package com.dataengi.crm.profiles.controllers.data

case class UpdateProfileData(nickname: String,
                             firstName: Option[String],
                             lastName: Option[String],
                             avatarUrl: Option[String])
