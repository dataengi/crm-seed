package com.dataengi.crm.identities.repositories

import java.util.concurrent.TimeUnit

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.runner.SpecificationsFinder
import play.api.libs.json.Json
import play.api.test.PlaySpecification

import scala.concurrent.duration.FiniteDuration

class JWTAuthenticatorRepositorySpec extends PlaySpecification with AuthenticationContext with SpecificationsFinder {

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
