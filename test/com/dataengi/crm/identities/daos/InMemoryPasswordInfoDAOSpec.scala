package com.dataengi.crm.identities.daos

import com.dataengi.crm.identities.context.AuthenticationContext
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import org.specs2.matcher.MatchResult
import play.api.test.PlaySpecification

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class InMemoryPasswordInfoDAOSpec extends PlaySpecification with AuthenticationContext {

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[PasswordInfoDAO].to[InMemoryPasswordInfoDAOImpl]
    )
  }

  lazy val passwordInfoDAO: PasswordInfoDAO = application.injector.instanceOf[PasswordInfoDAO]

  sequential

  "PasswordInfoDAO" should {

    "find/get not exist value" in {
      val loginInfo                = loginInfoArbitrary.arbitrary.sample.get
      val resultPasswordInfoFuture = passwordInfoDAO.find(loginInfo)
      val resultPasswordInfo       = Await.result(resultPasswordInfoFuture, 10 seconds)
      resultPasswordInfo.isEmpty === true
    }

    "add login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.sample.get
      addUserInfo(loginInfo)
    }

    "update login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.sample.get
      addUserInfo(loginInfo)
      val passwordInfoForUpdate                      = passwordInfoArbitrary.arbitrary.sample.get
      val updateUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.update(loginInfo, passwordInfoForUpdate)
      val updateUserInfo: PasswordInfo               = Await.result(updateUserInfoFuture, 10 seconds)
      updateUserInfo === passwordInfoForUpdate
    }

    "save login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.sample.get
      saveUserInfo(loginInfo)

      val passwordInfoForUpdate = passwordInfoArbitrary.arbitrary.sample.get

      val updateUserInfoFuture: Future[PasswordInfo] = passwordInfoDAO.save(loginInfo, passwordInfoForUpdate)
      val updateUserInfo: PasswordInfo               = Await.result(updateUserInfoFuture, 10 seconds)
      updateUserInfo === passwordInfoForUpdate
    }

    "remove login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.sample.get
      saveUserInfo(loginInfo)
      val removeLoginInfoFuture = passwordInfoDAO.remove(loginInfo)
      Await.result(removeLoginInfoFuture, 10 seconds)
      val resultPasswordInfoFuture = passwordInfoDAO.find(loginInfo)
      val resultPasswordInfo       = Await.result(resultPasswordInfoFuture, 10 seconds)
      resultPasswordInfo.isEmpty === true
    }
  }

  def addUserInfo(loginInfo: LoginInfo): MatchResult[PasswordInfo] = {
    val passwordInfo                      = passwordInfoArbitrary.arbitrary.sample.get
    val addUserInfo: Future[PasswordInfo] = passwordInfoDAO.add(loginInfo, passwordInfo)
    println(passwordInfo)
    Await.result(addUserInfo, 10 seconds) === passwordInfo
  }

  def saveUserInfo(loginInfo: LoginInfo): MatchResult[PasswordInfo] = {
    val passwordInfo                       = passwordInfoArbitrary.arbitrary.sample.get
    val saveUserInfo: Future[PasswordInfo] = passwordInfoDAO.save(loginInfo, passwordInfo)
    Await.result(saveUserInfo, 10 seconds) === passwordInfo
  }
}
