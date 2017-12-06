package com.dataengi.crm.common.context.types
import com.dataengi.crm.common.context.types.PlayResultExtensions.{AsyncResultExtension, ErrorResultExtension, XorExtension}
import com.dataengi.crm.common.errors.JsonValidationError
import com.dataengi.crm.identities.models.UserStates.UserState
import com.dataengi.crm.identities.models.{Company, Role}
import play.api.libs.json.{JsError, JsString, JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

trait PlayResult {
  type AsyncResult = Future[Result]
}

object PlayResultExtensions {

  final class XorExtension[T](val or: XorType[T]) extends AnyVal {

    def result: Result = or match {
      case Right(jsValue: JsValue) => Ok(jsValue)
      case Right(value: Result)    => value
      case Right(value: String)    => Ok(value)
      case Right(_: Empty)         => PlayResult.SUCCESS
      case Right(_)                => PlayResult.SUCCESS

      case Left(error: ForbiddenResult)     => PlayResult.forbidden(error.toJson)
      case Left(error: UnauthorizedResult)  => PlayResult.unauthorized(error.toJson)
      case Left(error: JsonValidationError) => PlayResult.jsonError(error.jsError)
      case Left(error: AppErrorResult)      => PlayResult.badRequest(error.toJson)
      case Left(error: JsValue)             => PlayResult.badRequest(error)
      case Left(error: AppError)            => PlayResult.failure(error.toString)
    }
  }

  final class AsyncResultExtension[T](val or: Or[T]) extends AnyVal {
    def asyncResult(implicit executionContext: ExecutionContext): AsyncResult = or.value.map(_.result)
  }

  final class ErrorResultExtension(val error: AppError) extends AnyVal {

    def result = error match {
      case error: ForbiddenResult     => PlayResult.forbidden(error.toJson)
      case error: UnauthorizedResult  => PlayResult.unauthorized(error.toJson)
      case error: JsonValidationError => PlayResult.jsonError(error.jsError)
      case error: AppErrorResult      => PlayResult.badRequest(error.toJson)
      case error: JsValue             => PlayResult.badRequest(error)
      case error: AppError            => PlayResult.failure(error.toString)
    }

  }

}

trait PlayResultExtensions {

  implicit def xorTypeExtension[T](or: XorType[T]): XorExtension[T] = new XorExtension[T](or)

  implicit def asyncResultExtension[T](or: Or[T]): AsyncResultExtension[T] = new AsyncResultExtension(or)

  implicit def errorResultExtension(error: AppError): ErrorResultExtension = new ErrorResultExtension(error)

}

object PlayResult {

  val UnauthorizedErrorMessageHeader: String = "X-Error-Message"

  private val STATUS  = "status"
  private val REASON  = "reason"
  private val MESSAGE = "message"

  private val WARNING = "warning"
  private val success = JsString("success")

  private val failure = JsString("failure")

  val SUCCESS = Ok(Json.obj(STATUS -> success))
  val FAILURE = Ok(Json.obj(STATUS -> failure))

  val UNAUTHORIZED = Unauthorized(Json.obj(MESSAGE -> "Access token expired"))

  val FORBIDDEN                 = Forbidden
  val FORBIDDEN_PERMISSION_DENY = forbidden("Permission deny")

  def failure(failureReason: String): Result = BadRequest(Json.obj(STATUS -> failure, REASON -> failureReason))

  def jsonError(jsErr: JsError): Result = BadRequest(Json.obj(STATUS -> failure, REASON -> JsError.toJson(jsErr)))

  def forbidden(forbiddenReason: String): Result = Forbidden(Json.obj(REASON -> forbiddenReason))

  def forbidden(forbidden: JsValue): Result = Forbidden(Json.obj(REASON -> forbidden))

  def badRequest(error: JsValue): Result = BadRequest(error)

  def unauthorized(message: JsValue) = Unauthorized(message)

  def success(message: String): Result    = success(JsString(message))
  def success(message: JsValue): Result   = Ok(Json.obj(MESSAGE -> message))
  def success(appError: AppError): Result = success(Json.obj(WARNING -> JsString(appError.toString)))
}

case class PlaySuccessResultResponse(status: String)
case class PlayFailureResultResponse(status: String, reason: String)
case class RecoverPasswordResultResponse(token: String)
case class UserIdentityResultResponse(email: String, company: Company, role: Role, state: UserState, id: Long)
