package com.dataengi.crm.identities.utils.mails

import java.util.UUID

import cats.instances.all._
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.util.IDGenerator
import com.dataengi.crm.common.configurations.links.LinksConfiguration
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.services.mailer.MailData

import scala.concurrent.ExecutionContext

@Singleton
class MailsCreator @Inject()(links: LinksConfiguration,
                             iDGenerator: IDGenerator,
                             implicit val executionContext: ExecutionContext) {

  def forgotPasswordEmail(hash: String): Or[MailData] =
    for {
      domain <- links.domain
      url    <- links.recoverPassword
    } yield {
      val totalLink = domain + url + hash
      val body =
        s"""
           |<p>You told us you forgot your password. If you really did, click here to choose a new one:</p> 
           |<a href="$totalLink">Choose a new password</a>
           |<p>If you didn't mean to reset your password, then you can just ignore this email; your password will not change.</p>""".stripMargin
      MailData("Recover password", Some(body), None)
    }

  def signUpInviteMail(hash: UUID): Or[MailData] = {
    for {
      domain <- links.domain
      url    <- links.signUpLink
    } yield {
      val totalLink = domain + url + hash.toString
      val body =
        s"""
           |<p>Thank you for registering at CRM. You may now log in by
           |clicking this link or copying and pasting it to your browser:</p>
           |<a href="$totalLink">$totalLink</a>
           |<p>This link can only be used once to log in and will lead you to a page where
           |you can set your password.</p>""".stripMargin
      MailData("Register invite", Some(body), None)
    }
  }

}
