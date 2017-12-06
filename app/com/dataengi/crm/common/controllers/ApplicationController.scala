package com.dataengi.crm.common.controllers

import play.api.mvc.{AbstractController, Result}
import com.dataengi.crm.common.context.types._
import cats.instances.all._
import play.api.libs.json.Writes
import com.dataengi.crm.common.extensions.formatters.JsonFormatterExtension._

import scala.concurrent.{ExecutionContext, Future}

abstract class ApplicationController(protected val components: play.api.mvc.ControllerComponents)
    extends AbstractController(components) {

  implicit def resultConversion[T: Writes](value: Or[T])(implicit executionContext: ExecutionContext): Future[Result] = value.map(_.toJson).asyncResult

  implicit def playResultConversion(value: Or[Result])(implicit executionContext: ExecutionContext): Future[Result] = value.asyncResult

  implicit def unitResultConversion(value: EmptyOr)(implicit executionContext: ExecutionContext): Future[Result] = value.asyncResult

}
