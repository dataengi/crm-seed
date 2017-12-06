package com.dataengi.crm.common.extensions
import org.scalacheck.{Arbitrary, Gen}

import scala.language.implicitConversions

/**
  * Created by nk91 on 23.03.17.
  */
package object arbitrary extends ArbitraryExtensions

trait ArbitraryExtensions {

  implicit def arbitraryValueExtensions[T](value: Arbitrary[T]): ArbitraryValueExtensions[T] =
    new ArbitraryValueExtensions(value)

  implicit def genValueExtension[T](value: Gen[T]): GenValueExtension[T] = new GenValueExtension(value)

}

class ArbitraryValueExtensions[T](val arbitrary: Arbitrary[T]) extends AnyVal {

  def value: T = arbitrary.arbitrary.sample.get

  def list(size: Int): List[T] = Gen.listOfN(size, arbitrary.arbitrary).sample.get

}

class GenValueExtension[T](val gen: Gen[T]) extends AnyVal {

  def value: T = gen.sample.get

  def list(size: Int): List[T] = Gen.listOfN(size, gen).sample.get

}
