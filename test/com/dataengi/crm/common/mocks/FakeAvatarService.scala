package com.dataengi.crm.common.mocks

import com.dataengi.crm.profiles.services.AvatarService
import com.dataengi.crm.common.context.types._

/**
  * Created by nk91 on 27.03.17.
  */
class FakeAvatarService extends AvatarService {
  override def retrieveURL(email: String): Or[Option[String]] = Option(s"http://fake.avatar.url.for.$email").toOr
}
