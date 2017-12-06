package com.dataengi.crm.common.arbitraries

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by nk91 on 17.03.17.
  */
trait CommonArbitrary {

  implicit val stringArbitrary: Arbitrary[String] = Arbitrary(Gen.alphaStr.map(_.capitalize.take(15)))

}
