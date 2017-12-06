package com.dataengi.crm.contacts.filters

import javax.inject._

import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ExampleFilter @Inject()(
    implicit override val mat: Materializer,
    exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {
    nextFilter(requestHeader).map { result =>
      result.withHeaders("X-ExampleFilter" -> "foo")
    }
  }

}
