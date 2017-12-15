package com.dataengi.crm.identities.daos

import com.dataengi.crm.identities.context.AuthenticationContext
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import org.specs2.mutable.Specification
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification
import com.dataengi.crm.common.extensions.types._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class InMemoryPasswordInfoDAOTest extends PlaySpecification with AuthenticationContext {

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[PasswordInfoDAO].to[InMemoryPasswordInfoDAOImpl]
    )
  }

  lazy val passwordInfoDAO = application.injector.instanceOf[PasswordInfoDAO]

  lazy val loginInfo = LoginInfo("testProviderId", "testProviderKey")
  lazy val passwordInfo = PasswordInfo("testHasher", "password", Some("testSalt"))

    sequential

  "PasswordInfoDAO" should {


    "find/get not exist value" in {
      val resultPasswordInfoFuture = passwordInfoDAO.find(loginInfo)
      val resultPasswordInfo = Await.result(resultPasswordInfoFuture, 10 seconds)
      resultPasswordInfo.isEmpty === true
    }

    "add login info" in {
      val addUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.add(loginInfo, passwordInfo)
      val addUserInfo: PasswordInfo = Await.result(addUserInfoFuture, 10 seconds)
      addUserInfo === passwordInfo
    }

    "update login info" in {
      val addUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.add(loginInfo, passwordInfo)
      val addUserInfo: PasswordInfo = Await.result(addUserInfoFuture, 10 seconds)
      addUserInfo === passwordInfo
      val updateUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.update(loginInfo, passwordInfo)
      val updateUserInfo: PasswordInfo =  Await.result(updateUserInfoFuture, 10 seconds)
      updateUserInfo === passwordInfo
    }

    "save login info" in {
      val saveUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.save(loginInfo, passwordInfo)
      val saveUserInfo: PasswordInfo = Await.result(saveUserInfoFuture, 10 seconds)
      saveUserInfo === passwordInfo

      val passwordInfoForUpdate = passwordInfo.copy(password = "newPass")

      val updateUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.save(loginInfo, passwordInfoForUpdate)
      val updateUserInfo: PasswordInfo = Await.result(updateUserInfoFuture, 10 seconds)
      updateUserInfo === passwordInfoForUpdate
    }

    "remove login info" in {
      val saveUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.save(loginInfo, passwordInfo)
      val saveUserInfo: PasswordInfo = Await.result(saveUserInfoFuture, 10 seconds)
      saveUserInfo === passwordInfo
      val removeLoginInfoFuture = passwordInfoDAO.remove(loginInfo)
      Await.result(removeLoginInfoFuture, 10 seconds)
      val resultPasswordInfoFuture = passwordInfoDAO.find(loginInfo)
      val resultPasswordInfo = Await.result(resultPasswordInfoFuture, 10 seconds)
      resultPasswordInfo.isEmpty === true
    }
  }
}