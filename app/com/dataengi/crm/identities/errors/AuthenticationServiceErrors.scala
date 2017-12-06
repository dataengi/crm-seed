package com.dataengi.crm.identities.errors

import com.mohiva.play.silhouette.api.exceptions.ConfigurationException
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.dataengi.crm.common.context.types.{AppErrorResult, ForbiddenResult}

sealed trait AuthenticationServiceAppError extends AppErrorResult
case class AuthenticationServiceError(description: String) extends AuthenticationServiceAppError {
  override def toString: String = description
}

case class AuthenticationForbiddenError(description: String) extends AuthenticationServiceAppError with ForbiddenResult

object AuthenticationServiceErrors {

  val SignUpUserAlreadyExist = AuthenticationServiceError("Sign up error. User already exist")

  val UserWithEmailNotFound = AuthenticationServiceError("User with this email doesn't exists")

  val RecoverPasswordInfoNotFound = AuthenticationServiceError("Recover password info not found")

  val InvalidPassword = AuthenticationServiceError("Invalid password for user")

  val ConfigurationError = AuthenticationServiceError("Configuration error")

  val IdentityNotFound = AuthenticationServiceError("User not found")

  val SignUpUrlAlreadyUsed = AuthenticationForbiddenError("Sign up url already used")

  val AuthenticateErrorsMapper: PartialFunction[Throwable, AppErrorResult] = {
    case error: InvalidPasswordException  => InvalidPassword
    case error: ConfigurationException    => ConfigurationError
    case error: IdentityNotFoundException => IdentityNotFound
  }

}
