package com.dataengi.crm.identities.arbitraries

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.dataengi.crm.common.arbitraries.CommonArbitrary
import com.dataengi.crm.identities.models.Actions.Action
import com.dataengi.crm.identities.models.InviteStatuses.InviteStatus
import com.dataengi.crm.identities.models._
import com.dataengi.crm.identities.models.PermissionStates.PermissionState
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

import scala.concurrent.duration.FiniteDuration

trait IdentitiesArbitrary extends CommonArbitrary {

  lazy val companyArbitrary: Arbitrary[Company] = Arbitrary(Gen.resultOf(Company))

  implicit val actionArbitrary: Arbitrary[Action]                   = Arbitrary(Gen.oneOf(Actions.values.toList))
  implicit val permissionStateArbitrary: Arbitrary[PermissionState] = Arbitrary(Gen.oneOf(PermissionStates.values.toList))
  implicit val permissionArbitrary: Arbitrary[Permission]           = Arbitrary(Gen.resultOf(Permission))
  implicit val roleArbitrary: Arbitrary[Role]                       = Arbitrary(Gen.resultOf(Role))
  implicit val inviteStatusArbitrary: Arbitrary[InviteStatus]       = Arbitrary(Gen.oneOf(InviteStatuses.values.toList))
  implicit val uuidArbitrary: Arbitrary[UUID]                       = Arbitrary(Gen.uuid)
  implicit val inviteArbitrary: Arbitrary[Invite]                   = Arbitrary(Gen.resultOf(Invite))

  val dateTimeGen = for {
    value <- Gen.Choose.chooseLong.choose(0, Long.MaxValue)
  } yield new DateTime(value)

  val finiteDurationGen = for {
    value <- Gen.Choose.chooseLong.choose(0, Long.MaxValue)
  } yield new FiniteDuration(value, TimeUnit.NANOSECONDS)

  val jsObject = Gen.oneOf(List(Some(Json.obj("a" -> "b")), None))

  implicit val jsObjectArbitrary       = Arbitrary(jsObject)
  implicit val dateTimeArbitrary       = Arbitrary(dateTimeGen)
  implicit val finiteDurationArbitrary = Arbitrary(finiteDurationGen)
  implicit val loginInfoArbitrary      = Arbitrary(Gen.resultOf(LoginInfo))
  implicit val authenticatorArbitrary  = Arbitrary(Gen.resultOf(JWTAuthenticator.apply _))

}
