package com.dataengi.crm.profiles.models

case class Profile(userId: Long,
                   nickname: String,
                   email: String,
                   firstName: Option[String] = None,
                   lastName: Option[String] = None,
                   avatarUrl: Option[String] = None,
                   id: Option[Long] = None)
