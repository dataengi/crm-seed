package com.dataengi.crm.common.extensions.awaits

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits.AwaitExtensions.{AwaitFutureExtension, AwaitOrExtension}

import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions
import scala.language.postfixOps

trait AwaitExtensions {

  implicit def awaitFutureExtension[T](future: Future[T]): AwaitFutureExtension[T] = new AwaitFutureExtension(future)

  implicit def awaitOrExtension[T](or: Or[T]): AwaitOrExtension[T] = new AwaitOrExtension(or)

}

object AwaitExtensions {

  final val DURATION = 10 seconds

  class AwaitFutureExtension[T](val future: Future[T]) extends AnyVal {

    def await(): T = {
      Await.result(future, DURATION)
    }

    def await(duration: Duration): T = {
      Await.result(future, duration)
    }

  }

  class AwaitOrExtension[T](val or: Or[T]) extends AnyVal {

    def await(): XorType[T] = {
      Await.result(or.value, DURATION)
    }

    def await(duration: Duration): XorType[T] = {
      Await.result(or.value, duration)
    }

  }

}
