package com.dataengi.crm.identities.context

import com.dataengi.crm.contacts.generators.EmailGen
import com.dataengi.crm.identities.arbitraries.IdentitiesArbitrary
import org.scalacheck.{Arbitrary, Gen}
import com.dataengi.crm.identities.controllers.data.{InviteAdvertiserData, InviteData}
import com.dataengi.crm.identities.services.{RolesService, UsersService}

trait InvitesServiceContext extends AuthenticationContext with IdentitiesArbitrary {

  val testCompany: String = "TEST_COMPANY_" + Gen.uuid.sample.get.toString

  val randomEmailsGenerator = EmailGen.randomEmailsGenerator

  def genInviteData(roleId: Long, companyId: Long): Gen[InviteData] =
    for {
      email <- randomEmailsGenerator
    } yield InviteData(email, roleId, companyId)

  def inviteDataArbitrary(roleId: Long, companyId: Long): Arbitrary[InviteData] =
    Arbitrary(genInviteData(roleId, companyId))

  def inviteAdvertiserDataArbitrary(): Arbitrary[InviteAdvertiserData] =
    Arbitrary(for { email <- randomEmailsGenerator } yield InviteAdvertiserData(email))

  lazy val usersServices: UsersService = application.injector.instanceOf[UsersService]
  lazy val rolesServices: RolesService = application.injector.instanceOf[RolesService]

}
