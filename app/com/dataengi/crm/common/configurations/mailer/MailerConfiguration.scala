package com.dataengi.crm.common.configurations.mailer

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.errors.ConfigErrors
import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class MailerConfiguration @Inject()(configuration: Configuration){

  def from(): Or[String]= {
    configuration.getOptional[String]("play.mailer.from").toOrWithLeftError(ConfigErrors.ConfigNotFoundError)
  }

}
