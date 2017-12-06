package com.dataengi.crm.common.extensions.types

import com.dataengi.crm.common.context.types._
import cats.instances.all._
import com.dataengi.crm.common.extensions.types.TupleOrExtension.Tuple2OrExtension
import scala.concurrent.ExecutionContext


trait TupleOrExtension {

  implicit def tuple2OrExtension[A, B](tupleOr: Or[(A, B)]): Tuple2OrExtension[A, B] = new Tuple2OrExtension(tupleOr)

}

object TupleOrExtension {

  class Tuple2OrExtension[A, B](val tuple2Or: Or[(A, B)]) extends AnyVal {

    def first(implicit ex: ExecutionContext): Or[A] = tuple2Or.map(_._1)
    def seconds(implicit ex: ExecutionContext): Or[B] = tuple2Or.map(_._2)

  }

}
