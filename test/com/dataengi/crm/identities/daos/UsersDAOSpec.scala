package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.context.{AuthenticationContext, CompaniesServiceContext, RolesServiceContext}
import com.dataengi.crm.identities.models.{Company, User, UserStates}
import com.mohiva.play.silhouette.api.LoginInfo
import org.scalacheck.Gen
import org.specs2.runner.SpecificationsFinder
import play.api.Application
import play.api.test.PlaySpecification

class UsersDAOSpec
    extends PlaySpecification
    with AuthenticationContext
    with CompaniesServiceContext
    with RolesServiceContext {

  "UsersDAOSpecification" should {

    "add user" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val getUserResult = usersDAO.add(users.head).await()
      getUserResult.isRight === true
    }

    "add userList" in {
      val fixture = new Fixture(application, userCount = 2)
      import fixture._

      val getUserResult = usersDAO.add(users.toList).await()

      val users1 = usersDAO.findByCompany(company.id.get).await().value

      users1 must have size 2
    }

    "find user by login info" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val getUserResult = usersDAO.add(users.head).await()

      val findUserResult = usersDAO.find(users.head.loginInfo).await()

      val user: User = findUserResult.value.get
      user.loginInfo === users.head.loginInfo
    }

    "find user by companyId negative" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val companyId      = company.id.get
      val findUserResult = usersDAO.findByCompany(companyId).await()

      val users = findUserResult.value

      users must have size 0
    }

    "all user" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._
      val allResult = usersDAO.all.await()
      allResult.isRight === true
    }

    "update user" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val TestLoginInfoUpdate: LoginInfo = LoginInfo("provider", users.head.loginInfo.providerKey)

      val getUserResult = usersDAO.add(users.head).await()

      val key = getUserResult.value

      val updateUser       = User(loginInfo = TestLoginInfoUpdate, company = company, role = role).copy(id = Some(key))
      val updateUserResult = usersDAO.update(updateUser).await()

      val getUser = usersDAO.get(key).await()

      getUser.value.loginInfo.providerID === TestLoginInfoUpdate.providerID
    }

    "update user negative" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val TestLoginInfo5: LoginInfo = LoginInfo("provider4", "dasd4@asd.com")

      val updateUser       = User(loginInfo = TestLoginInfo5, company = company, role = role, id = Some(56667))
      val updateUserResult = usersDAO.update(updateUser).await()
      updateUserResult.isLeft === true
    }

    "getOption user" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val getUserResult = usersDAO.add(users.head).await()

      val key = getUserResult.value

      val getOptionUserResult = usersDAO.getOption(key).await()

      getOptionUserResult.value.get.loginInfo === users.head.loginInfo
    }

    "update state user" in {
      val fixture = new Fixture(application, userCount = 1)
      import fixture._

      val getUserResult = usersDAO.add(users.head).await()

      val key = getUserResult.value

      val updateUserResult = usersDAO.updateState(key, UserStates.Activated).await()

      val getUser = usersDAO.get(key).await()
      getUser.value.state === UserStates.Activated
    }
  }

  class Fixture(application: Application, userCount: Int) {
    lazy val usersDAO = application.injector.instanceOf[UsersDAO]
    def createCompany(): Company = {
      val NewCompanyName: String          = "NEW_COMPANY" + Gen.alphaStr.sample.get
      val newCompanyResult: XorType[Long] = companiesService.create(NewCompanyName).await()
      val company: Company                = companiesService.get(newCompanyResult.value).await().value
      company.id must not be None
      company
    }

    lazy val company = createCompany()
    lazy val role    = rolesService.find(RolesConfiguration.CompanyManager.name).await().value

    lazy val users = (1 to userCount).map { _ =>
      val TestLoginInfo: LoginInfo =
        LoginInfo(
          "provider-" + Gen.alphaStr.sample.get,
          s"${Gen.alphaStr.sample.get}@asd.com"
        )

      User(
        loginInfo = TestLoginInfo,
        company = company,
        role = role
      )
    }
  }

}
