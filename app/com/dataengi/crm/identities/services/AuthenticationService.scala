package com.dataengi.crm.identities.services

import com.dataengi.crm.identities.controllers.data.{ForgotPassword, RecoverPasswordData, SignInData, SignUpData}
import com.dataengi.crm.identities.models.{InviteStatuses, RecoverPasswordInfo, RecoverPasswordInfoStatuses, User}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.services.mailer.MailService
import com.dataengi.crm.configurations.SilhouetteConfiguration
import com.dataengi.crm.identities.errors.{AuthenticationServiceErrors, UsersServiceErrors}
import com.dataengi.crm.identities.repositories.RecoverPasswordInfoRepository
import com.dataengi.crm.identities.utils.auth.DefaultEnv
import com.dataengi.crm.identities.utils.mails.MailsCreator
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, IDGenerator, PasswordHasher}
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.mvc.Request
import cats.instances.all._

import scala.concurrent.ExecutionContext

trait AuthenticationService {

  def forgotPassword(data: ForgotPassword, host: String): Or[RecoverPasswordInfo]

  def signIn(data: SignInData)(implicit request: Request[_]): Or[SignInInfo]

  def signUp(data: SignUpData, inviteUUID: String)(implicit request: Request[_]): Or[SignUpResult]

  def recoverPassword(data: RecoverPasswordData, recoverId: String)(implicit request: Request[_]): Or[String]

  def getRecoverPasswordUrl(recoverId: String): Or[String]

  def checkUserExist(email: String): EmptyOr
}

@Singleton
class AuthenticationServiceImplementation @Inject()(recoverPasswordInfoRepository: RecoverPasswordInfoRepository,
                                                    authInfoRepository: AuthInfoRepository,
                                                    userService: UsersService,
                                                    configuration: SilhouetteConfiguration,
                                                    invitesService: InvitesService,
                                                    rolesService: RolesService,
                                                    companiesService: CompaniesService,
                                                    mailService: MailService,
                                                    clock: Clock,
                                                    passwordHasher: PasswordHasher,
                                                    credentialsProvider: CredentialsProvider,
                                                    mailsCreator: MailsCreator,
                                                    iDGenerator: IDGenerator,
                                                    silhouette: Silhouette[DefaultEnv],
                                                    implicit val executionContext: ExecutionContext)
    extends AuthenticationService {

  override def signIn(data: SignInData)(implicit request: Request[_]): Or[SignInInfo] =
    for {
      loginInfo <- credentialsProvider
        .authenticate(Credentials(data.email, data.password))
        .toOrWithThrowableMap(AuthenticationServiceErrors.AuthenticateErrorsMapper)
      user <- userService.retrieve(loginInfo).toOrWithLeft(UsersServiceErrors.IdentityNotFound)
      token <- silhouette.env.authenticatorService
        .create(loginInfo)(request)
        .map(configureAuthenticator(_, data))
        .flatMap(silhouette.env.authenticatorService.init)
        .toOr
    } yield SignInInfo(loginInfo, user, token)

  private def configureAuthenticator(authenticator: JWTAuthenticator, data: SignInData): JWTAuthenticator = {
    if (data.rememberMe)
      authenticator.copy(
        expirationDateTime = clock.now + configuration.authenticatorExpiry,
        idleTimeout = configuration.authenticatorIdleTimeout
      )
    else authenticator
  }

  override def signUp(data: SignUpData, inviteUUID: String)(implicit request: Request[_]): Or[SignUpResult] =
    for {
      invite <- invitesService.getByHash(inviteUUID)
      loginInfo = LoginInfo(CredentialsProvider.ID, invite.email)
      checkExistUser <- userService.retrieve(loginInfo).toOr.flatMap(checkExistUser)
      company        <- companiesService.get(invite.companyId)
      user = User(loginInfo, company, invite.role)
      saveUser           <- userService.save(user)
      authInfo           <- authInfoRepository.add(loginInfo, passwordHasher.hash(data.password)).toOr
      authenticator      <- silhouette.env.authenticatorService.create(loginInfo).toOr
      token              <- silhouette.env.authenticatorService.init(authenticator).toOr
      inviteStatusUpdate <- invitesService.update(invite.copy(status = InviteStatuses.Registered))
    } yield SignUpResult(loginInfo, user, token)

  private def checkExistUser(userOption: Option[User]): EmptyOr = userOption match {
    case Some(user) => AuthenticationServiceErrors.SignUpUserAlreadyExist.toErrorOrWithType[Empty]
    case None       => EmptyOrValue
  }

  override def forgotPassword(data: ForgotPassword, host: String): Or[RecoverPasswordInfo] =
    for {
      user      <- userService.findByEmail(data.email).toOrWithLeft(AuthenticationServiceErrors.UserWithEmailNotFound)
      recoverId <- iDGenerator.generate.toOr
      recoverPasswordInfo = RecoverPasswordInfo(email = data.email,
                                                host = host,
                                                expiredDate = configuration.recoverPasswordTimeout,
                                                userId = user.id.get,
                                                recoverId = recoverId)
      recoverPasswordInfoId <- recoverPasswordInfoRepository.add(recoverPasswordInfo)
      forgotPasswordEmail   <- mailsCreator.forgotPasswordEmail(recoverId)
      sendEmail             <- mailService.sendEmail(data.email)(forgotPasswordEmail)
    } yield recoverPasswordInfo.copy(id = Some(recoverPasswordInfoId))

  override def recoverPassword(data: RecoverPasswordData, recoverId: String)(implicit request: Request[_]): Or[String] =
    for {
      recoverPasswordInfo <- recoverPasswordInfoRepository
        .getByRecoverId(recoverId)
        .toOrWithLeft(AuthenticationServiceErrors.RecoverPasswordInfoNotFound)
      user          <- userService.get(recoverPasswordInfo.userId)
      authInfo      <- authInfoRepository.update(user.loginInfo, passwordHasher.hash(data.newPassword)).toOr
      authenticator <- silhouette.env.authenticatorService.create(user.loginInfo)(request).toOr
      token         <- silhouette.env.authenticatorService.init(authenticator).toOr
      updateRecoverPasswordInfo <- recoverPasswordInfoRepository
        .update(recoverPasswordInfo.id.get, recoverPasswordInfo.copy(status = RecoverPasswordInfoStatuses.RECOVERED))
    } yield token

  override def getRecoverPasswordUrl(recoverId: String): Or[String] = {
    for { //todo check expired date
      recoverInfo <- recoverPasswordInfoRepository
        .getByRecoverId(recoverId)
        .toOrWithLeft(AuthenticationServiceErrors.RecoverPasswordInfoNotFound)
    } yield s"/#/auth/reset?email=${recoverInfo.email}&hash=$recoverId" //todo move url to config
  }

  override def checkUserExist(email: String): EmptyOr = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, email)
    userService.retrieve(loginInfo).toOr.flatMap(checkExistUser)
  }
}

case class SignInResult(token: String, loginInfo: LoginInfo)

case class SignInInfo(loginInfo: LoginInfo, user: User, token: String)

case class SignUpResult(loginInfo: LoginInfo, user: User, token: String)
