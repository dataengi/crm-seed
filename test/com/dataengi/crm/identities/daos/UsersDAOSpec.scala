package com.dataengi.crm.identities.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.context.{AuthenticationContext, CompaniesServiceContext, RolesServiceContext}
import com.dataengi.crm.identities.models.{User, UserStates, Company}
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

  lazy val usersDAO = application.injector.instanceOf[UsersDAO]
  def createCompany(): Company = {
    val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
    val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
    val company: Company                = companiesService.get(newCompanyResult.value).await().value

    company.id must not be None

    company
  }

  "UsersDAOSpecification" should {

    "add user" in {

      val role                     = rolesService.find(RolesConfiguration.CompanyManager.name).await().value
      val company                  = createCompany()
      val TestLoginInfo: LoginInfo = LoginInfo("provider", "dasd@asd.com")
      val user: User = User(
        loginInfo = TestLoginInfo,
        company = company,
        role = role
      )

      val getUserResult = usersDAO.add(user).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

    }

    "add userList" in {
      val company                   = createCompany()
      val role                      = rolesService.find(RolesConfiguration.CompanyManager.name).await().value
      val TestLoginInfo: LoginInfo  = LoginInfo("provider", "dasd@asd.com")
      val TestLoginInfo2: LoginInfo = LoginInfo("provider2", "dasd2@asd.com")
      val user = User(
        loginInfo = TestLoginInfo,
        company = company,
        role = role
      )
      val user2 = User(
        loginInfo = TestLoginInfo2,
        company = company,
        role = role
      )

      val userList = List(user, user2)

      company.id must not be None

      val companyId = company.id.get

      val getUserResult = usersDAO.add(userList).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val users = usersDAO.findByCompany(companyId).await().right.get

      users must have size 2

      users.filter((user: User) => user.loginInfo.providerID == "provider") must have size 1
      users.filter((user: User) => user.loginInfo.providerID == "provider2") must have size 1

    }

    "find user by login info" in {

      val TestLoginInfo: LoginInfo = LoginInfo("provider", "dasd@asd.com")

      val findUserResult = usersDAO.find(TestLoginInfo).await()
      println(s"[user-dao][find] ${findUserResult.logResult}")
      findUserResult.isRight === true
      val user = findUserResult.value.get
      user.loginInfo === TestLoginInfo
    }

    "find user by companyId negative" in {
      val company = createCompany()
      val role    = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      company.id must not be None

      val companyId      = company.id.get
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
      val company = createCompany()
      val role    = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo3: LoginInfo = LoginInfo("provider3", "dasd3@asd.com")
      val TestLoginInfo4: LoginInfo = LoginInfo("provider4", "dasd3@asd.com")

      val user3 = User(
        loginInfo = TestLoginInfo3,
        company = company,
        role = role
      )

      val getUserResult = usersDAO.add(user3).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val key = getUserResult.value

      val updateUser       = User(loginInfo = TestLoginInfo4, company = company, role = role).copy(id = Some(key))
      val updateUserResult = usersDAO.update(updateUser).await()
      updateUserResult.isRight === true

      val getUser = usersDAO.get(key).await()
      getUser.isRight === true
      getUser.value.loginInfo.providerKey === TestLoginInfo4.providerKey

    }

    "update user negative" in {
      val company = createCompany()
      val role    = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo5: LoginInfo = LoginInfo("provider4", "dasd4@asd.com")

      val updateUser       = User(loginInfo = TestLoginInfo5, company = company, role = role, id = Some(56667))
      val updateUserResult = usersDAO.update(updateUser).await()
      updateUserResult.isLeft === true

    }

    "getOption user" in {
      val company = createCompany()
      val role    = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo: LoginInfo = LoginInfo("provider", "dasd@asd.com")
      val user = User(
        loginInfo = TestLoginInfo,
        company = company,
        role = role
      )

      val getUserResult = usersDAO.add(user).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val key = getUserResult.right.get

      val getOptionUserResult = usersDAO.getOption(key).await()
      getOptionUserResult.isRight === true

      getOptionUserResult.value.get.loginInfo === user.loginInfo
    }

    "update state user" in {
      val company = createCompany()
      val role    = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

      val TestLoginInfo: LoginInfo = LoginInfo("provider", "dasd@asd.com")

      val user = User(
        loginInfo = TestLoginInfo,
        company = company,
        role = role
      )

      val getUserResult = usersDAO.add(user).await()
      println(s"[user-dao][get] ${getUserResult.logResult}")
      getUserResult.isRight === true

      val key = getUserResult.value

      val updateUserResult = usersDAO.updateState(key, UserStates.Activated).await()
      updateUserResult.isRight === true

      val getUser = usersDAO.get(key).await()
      getUser.isRight === true
      getUser.value.state === UserStates.Activated

    }
  }

}
