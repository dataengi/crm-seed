package com.dataengi.crm.identities.controllers.swagger

import com.dataengi.crm.common.context.types.{PlaySuccessResultResponse, RecoverPasswordResultResponse, UserIdentityResultResponse}
import com.dataengi.crm.identities.controllers.data.{ForgotPassword, RecoverPasswordData, SignInData, SignUpData}
import com.dataengi.crm.identities.models.RecoverPasswordInfo
import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import play.api.mvc.{Action, AnyContent}
import scalty.results.ErrorResult

trait AuthenticationApiSwaggerDescription {

  @ApiOperation(value = "Sign in with email and password", httpMethod = "POST")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[SignInData], message = "Authentication data"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def signIn: Action[SignInData]
  @ApiOperation(value = "Sign up with email and password", httpMethod = "POST")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[SignUpData], message = "Authentication data"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def signUp(inviteUUID: String): Action[SignUpData]

  @ApiOperation(value = "Forgot password", httpMethod = "POST")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[RecoverPasswordInfo], message = "Information for password recovery"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def forgotPassword: Action[ForgotPassword]

  @ApiOperation(value = "Recover password URL", httpMethod = "POST") //
  @ApiResponses(
    Array(
      new ApiResponse(code = 302, response = classOf[String], message = "Recover password URL "), // work incorrect
      new ApiResponse(code = 403, response = classOf[ErrorResult], message = "URL is not active") //
    ))
  def toRecoverPassword(id: String): Action[AnyContent]

  @ApiOperation(value = "Recover password", httpMethod = "POST")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200,
                      response = classOf[RecoverPasswordResultResponse],
                      message = "Information about password recovery"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def recoverPassword(recoverId: String): Action[RecoverPasswordData]

  @ApiOperation(value = "SignOut", httpMethod = "POST")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success")
    ))
  def signOut: Action[AnyContent]
  @ApiOperation(value = "Authentication identity", httpMethod = "GET")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[UserIdentityResultResponse], message = "Success"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def identify: Action[AnyContent]

}
