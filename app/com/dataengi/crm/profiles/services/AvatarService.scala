package com.dataengi.crm.profiles.services

import java.net.URLEncoder._
import java.security.MessageDigest

import cats.instances.all._
import com.dataengi.crm.profiles.services.errors.ProfileServiceErrors
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

trait AvatarService {

  def retrieveURL(email: String): Or[Option[String]]

}

@Singleton
class AvatarServiceImplementation @Inject()(wSClient: WSClient,
                                            implicit val executionContext: ExecutionContext,
                                            settings: GravatarServiceSettings)
    extends AvatarService {

  override def retrieveURL(email: String): Or[Option[String]] = hash(email) match {
    case Some(hash) =>
      val url = settings.getUrl(hash)
      gerAvatarUrl(url)
    case None => ProfileServiceErrors.avatarCreatingUrlError(email).toErrorOr
  }

  private def gerAvatarUrl(url: String): Or[Option[String]] =
    wSClient
      .url(url)
      .get()
      .toOr
      .flatMap(response =>
        response.status match {
          case 200  => Option(url).toOr
          case code => ProfileServiceErrors.avatarReceivingError(code, url).toErrorOr
      })

  private def hash(email: String): Option[String] = {
    val s = email.trim.toLowerCase
    if (s.length > 0) {
      Some(MessageDigest.getInstance(GravatarService.MD5).digest(s.getBytes).map("%02x".format(_)).mkString)
    } else {
      None
    }
  }

}

object GravatarService {
  val InsecureURL = "http://www.gravatar.com/avatar/%s%s"
  val SecureURL   = "https://secure.gravatar.com/avatar/%s%s"
  val MD5         = "MD5"
}

case class GravatarServiceSettings(secure: Boolean = true, params: Map[String, String] = Map("d" -> "identicon")) {

  def getUrl(hash: String): String = {
    val encodedParams = params.map { p =>
      encode(p._1, "UTF-8") + "=" + encode(p._2, "UTF-8")
    }
    (if (secure) GravatarService.SecureURL else GravatarService.InsecureURL)
      .format(hash, encodedParams.mkString("?", "&", ""))
  }

}
