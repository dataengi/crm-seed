package com.dataengi.crm.common.extensions

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.errors.{FormValidationError, JsonValidationError}
import com.dataengi.crm.common.extensions.RequestsExtensions.JsonRequestValidateExtension
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.Request

import scala.language.implicitConversions

package object requests extends RequestsExtensions

trait RequestsExtensions {

  implicit def jsonRequestValidateExtension(request: Request[JsValue]): JsonRequestValidateExtension =
    new JsonRequestValidateExtension(request)

}

object RequestsExtensions {

  class JsonRequestValidateExtension(val request: Request[JsValue]) extends AnyVal {

    def validate[T](implicit rds: Reads[T]): JsResult[T] = request.body.validate[T](rds)

    def validateOpt[T](implicit rds: Reads[T]): JsResult[Option[T]] = request.body.validateOpt[T](rds)

    def validateOr[T](implicit rds: Reads[T]): Or[T] = request.body.validate[T] match {
      case JsSuccess(value, path) => value.toOr
      case jsError: JsError       => JsonValidationError(jsError).toErrorOr
    }

    def validateFormOr[T](implicit form: Form[T]): Or[T] =
      form
        .bindFromRequest()(request)
        .fold(
          hasErrors => FormValidationError(hasErrors.errors).toErrorOrWithType[T],
          value => value.toOr
        )

  }

}
