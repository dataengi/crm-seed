package com.dataengi.crm.identities.context

import com.dataengi.crm.common.context.CRMApplication
import com.dataengi.crm.configurations.{RolesConfiguration, RootConfiguration}
import com.dataengi.crm.identities.errors.UsersServiceErrors
import com.dataengi.crm.contacts.generators.EmailGen
import org.scalacheck.Gen
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import com.dataengi.crm.contacts.services._
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.arbitraries.IdentitiesArbitrary
import com.dataengi.crm.identities.controllers.data._
import com.dataengi.crm.identities.models.User
import com.dataengi.crm.identities.services._

trait AuthenticationContext extends CRMApplication with IdentitiesArbitrary {

  lazy val authenticationService = application.injector.instanceOf[AuthenticationService]
  lazy val invitesService        = application.injector.instanceOf[InvitesService]
  lazy val rolesService          = application.injector.instanceOf[RolesService]
  lazy val companiesService      = application.injector.instanceOf[CompaniesService]
  lazy val usersService          = application.injector.instanceOf[UsersService]
  lazy val rootConfiguration     = application.injector.instanceOf[RootConfiguration]
  lazy val rolesConfiguration    = application.injector.instanceOf[RolesConfiguration]

  lazy val rootSignInData =
    SignInData(rootConfiguration.rootLoginInfo.providerKey, rootConfiguration.rootPassword)

  lazy val TestCompanyName = "TEST_COMPANY" + Gen.alphaStr.sample.get

  lazy val userEmail             = EmailGen.randomEmailsGenerator.sample.getOrElse("test.eamail@gamil.com")
  lazy val userPassword          = "test_password"
  lazy val userSignInData        = SignInData(userEmail, userPassword)
  lazy val userSignUpData        = SignUpData(userPassword)
  lazy val forgotPassword        = ForgotPassword(userEmail)
  lazy val userLocalhost         = "localhost"
  lazy val newPassword: String   = "new_password"
  lazy val recoverPassword       = RecoverPasswordData(newPassword)
  lazy val userChangedSignInData = SignInData(userEmail, newPassword)
  lazy val rootUser: User = {
    usersService
      .findByEmail(rootConfiguration.rootLoginInfo.providerKey)
      .toOrWithLeft(UsersServiceErrors.IdentityNotFound)
      .await()
      .value
  }

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  def inviteData(testCompanyId: Long, salesRepresentativeRoleId: Long): InviteData = InviteData(
    email = userEmail,
    companyId = testCompanyId,
    roleId = salesRepresentativeRoleId
  )

}
