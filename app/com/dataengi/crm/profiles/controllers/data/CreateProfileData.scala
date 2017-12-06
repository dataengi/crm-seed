package com.dataengi.crm.profiles.controllers.data

case class CreateProfileData(userId: Long,
                             nickname: String,
                             firstName: Option[String] = None,
                             lastName: Option[String] = None,
                             avatarUrl: Option[String] = None)
