package com.dataengi.crm.identities.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.context.{AuthenticationContext, CompaniesServiceContext, RolesServiceContext}
import com.dataengi.crm.identities.models.{User, UserStates}
import org.scalacheck.Gen
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

class UsersDAOSpec
    extends PlaySpecification
    with AuthenticationContext
    with CompaniesServiceContext
    with RolesServiceContext
    with SpecificationsFinder {

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[RolesDAO].to[RolesSlickDAOImplementation]
    )
  }

  val TestLoginInfo: LoginInfo  = LoginInfo("provider", "dasd@asd.com")
  val TestLoginInfo2: LoginInfo = LoginInfo("provider2", "dasd2@asd.com")
  lazy val usersDAO             = application.injector.instanceOf[UsersDAO]

  "UsersDAOSpecification" should {

    "add user" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val user = User(
        loginInfo = TestLoginInfo,
        company = newCompany,
        role = role
      )

      val getUserResult = usersDAO.add(user).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true
    }

    "add userList" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val user = User(
        loginInfo = TestLoginInfo,
        company = newCompany,
        role = role
      )
      val user2 = User(
        loginInfo = TestLoginInfo2,
        company = newCompany,
        role = role
      )

      val userList = List(user, user2)

      newCompany.id must not be None

      val companyId = newCompany.id.get

      val getUserResult = usersDAO.add(userList).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val users = usersDAO.findByCompany(companyId).await().right.get

      users must have size 2

      users.filter((user: User) => user.loginInfo.providerID == "provider") must have size 1
      users.filter((user: User) => user.loginInfo.providerID == "provider2") must have size 1

    }

    "find user by login info" in {
      val findUserResult = usersDAO.find(TestLoginInfo).await()
      println(s"[user-dao][find] ${findUserResult.logResult}")
      findUserResult.isRight === true
      val user = findUserResult.value.get
      user.loginInfo === TestLoginInfo
    }

    "find user by companyId negative" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      newCompany.id must not be None

      val companyId      = newCompany.id.get
      val findUserResult = usersDAO.findByCompany(companyId).await()
      println(s"[user-dao][find] ${findUserResult.logResult}")
      findUserResult.isRight === true

      val users = findUserResult.value

      users must have size 0

    }

    "all user" in {
      val allResult = usersDAO.all.await()
      println(s"[user-dao][all] ${allResult.logResult}")
      allResult.isRight === true
    }

    "update user" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo3: LoginInfo = LoginInfo("provider3", "dasd3@asd.com")
      val TestLoginInfo4: LoginInfo = LoginInfo("provider4", "dasd3@asd.com")

      val user3 = User(
        loginInfo = TestLoginInfo3,
        company = newCompany,
        role = role
      )

      val getUserResult = usersDAO.add(user3).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val key = getUserResult.right.get

      val updateUser       = User(loginInfo = TestLoginInfo4, company = newCompany, role = role).copy(id = Some(key))
      val updateUserResult = usersDAO.update(updateUser).await()
      updateUserResult.isRight === true

      val getUser = usersDAO.get(key).await()
      getUser.isRight === true
      getUser.value.loginInfo.providerKey === TestLoginInfo4.providerKey

    }

    "update user negative" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo5: LoginInfo = LoginInfo("provider4", "dasd4@asd.com")

      val updateUser       = User(loginInfo = TestLoginInfo5, company = newCompany, role = role).copy(id = Some(56667))
      val updateUserResult = usersDAO.update(updateUser).await()
      updateUserResult.isLeft === true

    }

    "getOption user" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val user = User(
        loginInfo = TestLoginInfo,
        company = newCompany,
        role = role
      )

      val getUserResult = usersDAO.add(user).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val key = getUserResult.right.get

      val getOptionUserResult = usersDAO.getOption(key).await()
      getOptionUserResult.isRight === true

      getOptionUserResult.right.get.get.loginInfo === user.loginInfo
    }

    "update state user" in {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val newCompany                      = companiesService.get(newCompanyResult.value).await().value
      val role                            = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo: LoginInfo = LoginInfo("provider", "dasd@asd.com")

      val user = User(
        loginInfo = TestLoginInfo,
        company = newCompany,
        role = role
      )

      val getUserResult = usersDAO.add(user).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val key = getUserResult.right.get

      val updateUserResult = usersDAO.updateState(key, UserStates.Activated).await()
      updateUserResult.isRight === true

      val getUser = usersDAO.get(key).await()
      getUser.isRight === true
      getUser.value.state === UserStates.Activated

    }
  }

}
