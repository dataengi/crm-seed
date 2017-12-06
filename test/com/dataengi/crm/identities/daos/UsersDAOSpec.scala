package com.dataengi.crm.identities.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.context.{AuthenticationContext, CompaniesServiceContext, RolesServiceContext}
import com.dataengi.crm.identities.models.User
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

  val TestLoginInfo: LoginInfo = LoginInfo("provider", "dasd@asd.com")
  lazy val usersDAO            = application.injector.instanceOf[UsersDAO]

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

    "find user by login info" in {
      val findUserResult = usersDAO.find(TestLoginInfo).await()
      println(s"[user-dao][find] ${findUserResult.logResult}")
      findUserResult.isRight === true
      val user = findUserResult.value.get
      user.loginInfo === TestLoginInfo
    }

    "all user" in {
      val allResult = usersDAO.all.await()
      println(s"[user-dao][all] ${allResult.logResult}")
      allResult.isRight === true
    }

  }

}
