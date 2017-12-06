package com.dataengi.crm.common.services.mailer

import com.google.inject.{ImplementedBy, Inject}
import com.dataengi.crm.common.configurations.mailer.MailerConfiguration
import com.dataengi.crm.common.context.types._
import play.api.libs.mailer.{Email, MailerClient}
import cats.instances.all._
import scala.concurrent.ExecutionContext

case class MailData(subject: String, bodyHtml: Option[String], bodyText: Option[String])

@ImplementedBy(classOf[MailServiceImplementation]) //TODO move to module conf
trait MailService {
  def sendEmail(recipients: String*)(mailData: MailData): Or[String]
}

class MailServiceImplementation @Inject()(mailerClient: MailerClient,
                                          configuration: MailerConfiguration,
                                          implicit val executionContext: ExecutionContext)
    extends MailService {

  override def sendEmail(recipients: String*)(mailData: MailData): Or[String] = {
    for {
      from <- configuration.from()
    } yield {
      mailerClient.send(Email(mailData.subject, from, recipients, mailData.bodyText, mailData.bodyHtml))
    }
  }

}
