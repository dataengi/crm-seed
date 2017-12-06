package com.dataengi.crm.configurations

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.util.Clock
import com.typesafe.config.Config
import play.api.Configuration
import net.ceedubs.ficus.Ficus._
import com.mohiva.play.silhouette.api.Authenticator.Implicits._

import scala.concurrent.duration.FiniteDuration

@Singleton
class SilhouetteConfiguration @Inject()(configuration: Configuration, clock: Clock) {

  val underlying: Config = configuration.underlying

  def authenticatorExpiry: FiniteDuration =
    underlying.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry")
  def authenticatorIdleTimeout: Option[FiniteDuration] =
    underlying.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")

  def recoverPasswordTimeout: Long =
    (clock.now + underlying.as[FiniteDuration]("silhouette.authenticator.recoverPasswordExpiry")).getMillis

}
