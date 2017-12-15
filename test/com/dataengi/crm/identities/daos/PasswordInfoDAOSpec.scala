package com.dataengi.crm.identities.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

class PasswordInfoDAOSpec extends PlaySpecification with AuthenticationContext with SpecificationsFinder {

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[PasswordInfoDAO].to[PasswordInfoDAOSlickImplementation]
    )
  }

  lazy val passwordInfoDAO = application.injector.instanceOf[PasswordInfoDAO]

  lazy val loginInfo    = LoginInfo("testProviderId", "testProviderKey")
  lazy val passwordInfo = PasswordInfo("testHasher", "password", Some("testSalt"))

  "get not exist value" in {
    val resultPasswordInfo = passwordInfoDAO.find(loginInfo).await()
    resultPasswordInfo.isEmpty === true
  }
}
