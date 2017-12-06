package com.dataengi.crm.contacts.generators

import org.scalacheck.Gen

/**
  * Created by nk91 on 25.01.17.
  */
object EmailGen {

  val randomEmailsGenerator = for {
    prefix <- Gen.alphaStr.retryUntil(_.nonEmpty)
    domain <- Gen.oneOf("gmail.com", "ukr.net", "mail.ru", "mock.mock")
  } yield s"test.t$prefix@$domain"

}
