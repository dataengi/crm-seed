package com.dataengi.crm.identities.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.configurations.RolesConfiguration.SalesRepresentative
import com.dataengi.crm.identities.context.AuthenticationContext
import play.api.test.PlaySpecification

class AuthenticationServiceSpec extends PlaySpecification with AuthenticationContext {

  sequential

  "AuthenticationService" should {

    "sign in root user" in {
      val signInResult = authenticationService.signIn(rootSignInData).await()
      println(s"[root-sign-in] $signInResult")
      signInResult.isRight === true
      signInResult.value match {
        case SignInInfo(loginInfo, user, token) => loginInfo === rootConfiguration.rootLoginInfo
      }
    }

    s"create invite from root" in {
      val retrieveResult = usersService.retrieve(rootConfiguration.rootLoginInfo).await()
      retrieveResult.isDefined === true
      println(s"[root-sign-up][retrieve $retrieveResult")
      val rootUser                      = retrieveResult.get
      val testCompanyId                 = companiesService.create(TestCompanyName).await().value
      val roleSalesRepresentativeResult = rolesService.find(SalesRepresentative.name).await()
      println(s"[create-invite][role] ${roleSalesRepresentativeResult}")
      roleSalesRepresentativeResult.isRight === true
      val salesRepresentativeRoleId = roleSalesRepresentativeResult.value.id.get
      val userInviteData            = inviteData(testCompanyId, salesRepresentativeRoleId)
      val createInviteResult        = invitesService.create(userInviteData, rootUser.id.get).await()
      createInviteResult.isRight === true
      val signUpResult =
        authenticationService.signUp(userSignUpData, createInviteResult.value.hash.toString).await()
      println(s"[root-sign-up] $signUpResult")
      signUpResult.isRight === true
    }

    s"sig in invited user=$userSignInData" in {
      val signInResult = authenticationService.signIn(userSignInData).await()
      println(s"[invited-user-sign-in] $userSignInData")
      signInResult.isRight === true
    }

    "forgot and recover password" in {
      val forgotPasswordResult = authenticationService.forgotPassword(forgotPassword, userLocalhost).await()
      println(s"[forgot-password][forgot] $forgotPasswordResult")
      forgotPasswordResult.isRight === true
      val recoverPasswordResult =
        authenticationService.recoverPassword(recoverPassword, forgotPasswordResult.value.recoverId).await()
      println(s"[forgot-password][recover] $recoverPasswordResult")
      recoverPasswordResult.isRight === true
    }

    "sign in with recovered password" in {
      val signInResult = authenticationService.signIn(userChangedSignInData).await()
      println(s"[user-sign-in-with-changed-password] $userSignInData")
      println(s"[user-sign-in-with-changed-password][resutl] $signInResult ")
      signInResult.isRight === true
    }

  }

}
