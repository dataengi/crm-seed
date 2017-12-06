package com.dataengi.crm.common.configurations.links

import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.errors.ConfigErrors
import play.api.Configuration

@Singleton
class LinksConfiguration @Inject()(configuration: Configuration) {

  def domain: Or[String] = {
    configuration.getOptional[String]("link.domain").toOrWithLeftError(ConfigErrors.ConfigNotFoundError)
  }

  def signUpLink: Or[String] = {
    configuration.getOptional[String]("link.signUp").toOrWithLeftError(ConfigErrors.ConfigNotFoundError)
  }

 def recoverPassword: Or[String] = {
    configuration.getOptional[String]("link.recoverPassword").toOrWithLeftError(ConfigErrors.ConfigNotFoundError)
  }

}
