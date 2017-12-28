package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.extensions.arbitrary._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import com.dataengi.crm.identities.models.User
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.test.PlaySpecification

class PasswordInfoDAOSpec extends PlaySpecification with AuthenticationContext {

  lazy val passwordInfoDAO: PasswordInfoDAO = application.injector.instanceOf[PasswordInfoDAO]
  lazy val usersDAO: UsersDAO               = application.injector.instanceOf[UsersDAO]

  sequential

  "PasswordInfoDAO" should {

    "find/get not exist value" in {
      val loginInfo          = loginInfoArbitrary.value
      val resultPasswordInfo = passwordInfoDAO.find(loginInfo).await()
      resultPasswordInfo.isEmpty === true
    }

    "add login info" in {
      val loginInfo          = loginInfoArbitrary.value
      val passwordInfo       = addUserInfo(loginInfo)
      val resultPasswordInfo = passwordInfoDAO.find(loginInfo).await()
      resultPasswordInfo must beSome(passwordInfo)
    }

    "update login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.value
      addUserInfo(loginInfo)
      val passwordInfoForUpdate = passwordInfoArbitrary.arbitrary.value
      val updateUserInfo        = passwordInfoDAO.update(loginInfo, passwordInfoForUpdate).await()
      updateUserInfo === passwordInfoForUpdate
    }

    "save login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.value
      saveUserInfo(loginInfo)
      val passwordInfoForUpdate = passwordInfoArbitrary.arbitrary.value
      val updateUserInfo        = passwordInfoDAO.save(loginInfo, passwordInfoForUpdate).await()
      updateUserInfo === passwordInfoForUpdate
    }

    "remove login info" in {
      val loginInfo = loginInfoArbitrary.arbitrary.value
      saveUserInfo(loginInfo)
      passwordInfoDAO.remove(loginInfo).await()
      val resultPasswordInfo = passwordInfoDAO.find(loginInfo).await()
      resultPasswordInfo.isEmpty === true
    }
  }

  def addUserInfo(loginInfo: LoginInfo): PasswordInfo = {
    addUser(loginInfo)
    val passwordInfo = passwordInfoArbitrary.arbitrary.value
    passwordInfoDAO.add(loginInfo, passwordInfo).await()
  }

  def saveUserInfo(loginInfo: LoginInfo): PasswordInfo = {
    addUser(loginInfo)
    val passwordInfo = passwordInfoArbitrary.arbitrary.value
    passwordInfoDAO.save(loginInfo, passwordInfo).await()
  }

  private def addUser(loginInfo: LoginInfo) = {
    val user = User(loginInfo, companyArbitrary.arbitrary.value, roleArbitrary.arbitrary.value)
    usersDAO.add(user).await()
  }

}
