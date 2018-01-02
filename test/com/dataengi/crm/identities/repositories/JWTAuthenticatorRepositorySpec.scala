package com.dataengi.crm.identities.repositories

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import play.api.test.PlaySpecification

class JWTAuthenticatorRepositorySpec extends PlaySpecification with AuthenticationContext {

  lazy val jwtAuthenticatorRepository = application.injector.instanceOf[JWTAuthenticatorRepository]

  "JWTAuthenticatorDAO" should {

    "add" in {
      val authenticator = authenticatorArbitrary.arbitrary.sample.get
      val result        = jwtAuthenticatorRepository.add(authenticator).await()

      println(s"[jwt-repository][add] result=${result}")

      val getResult = jwtAuthenticatorRepository.find(authenticator.id).await()

      println(s"[jwt-repository][find] result=${getResult}")

      getResult.isDefined === true
      getResult.get.id === authenticator.id
      getResult.get.loginInfo === authenticator.loginInfo
    }

  }

}
