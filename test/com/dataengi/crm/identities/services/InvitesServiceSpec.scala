package com.dataengi.crm.identities.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.context.InvitesServiceContext
import com.dataengi.crm.identities.models.Invite
import play.api.test.PlaySpecification

class InvitesServiceSpec extends PlaySpecification with InvitesServiceContext {

  sequential

  "InvitesService" should {

    "create invite" in {
      val createResult    = createInvite()
      val getInviteResult = invitesService.get(createResult.value.id.get).await()
      getInviteResult.isRight === true
      createResult.value.role === getInviteResult.value.role
    }

    "create advertiser invite" in {
      val createResult    = createAdvertiserInvite()
      val getInviteResult = invitesService.get(createResult.value.id.get).await()
      getInviteResult.isRight === true
      createResult.value.role === getInviteResult.value.role
      createResult.value.role.name === RolesConfiguration.Advertiser.name
    }

    "get not exist invite" in {
      val getInviteResult = invitesService.get(Long.MaxValue).await()
      println(s"[get-invite-by-id] result =$getInviteResult")
      getInviteResult.isLeft === true
    }

    "get invite by hash" in {
      val createResult          = createInvite()
      val getInviteByHashResult = invitesService.getByHash(createResult.value.hash).await()
      println(s"[get-invite-by-hash] result=$getInviteByHashResult")
      getInviteByHashResult.isRight === true
      createResult.value.role === getInviteByHashResult.value.role
      createResult.value.companyId === getInviteByHashResult.value.companyId
      createResult.value.email === getInviteByHashResult.value.email
    }

    "get invites by companyId" in {
      val createResult                = createInvite()
      val getInvitesByCompanyIdResult = invitesService.getByCompanyId(createResult.value.companyId).await()
      println(s"[get-invites-by-companyId] result=$getInvitesByCompanyIdResult")
      getInvitesByCompanyIdResult.isRight === true

      val invites: List[Invite] = getInvitesByCompanyIdResult.value

      invites.contains(createResult.value) === true
    }

    "get invites by user which invite" in {
      val createResult             = createInvite()
      val getInvitesByUserIdResult = invitesService.getByUserIdWhichInvite(createResult.value.invitedBy).await()
      println(s"[get-invites-by-user-which-invites] result=$getInvitesByUserIdResult")
      getInvitesByUserIdResult.isRight === true
      getInvitesByUserIdResult.value must contain(createResult.value)
    }

    "update invite" in {
      val createResult    = createInvite()
      val getInviteResult = invitesService.get(createResult.value.id.get).await()
      getInviteResult.isRight === true
      val existInvite        = getInviteResult.value
      val updatedInvite      = inviteArbitrary.arbitrary.sample.get.copy(role = existInvite.role)
      val updateInviteResult = invitesService.update(existInvite.id.get, updatedInvite).await()
      println(s"[update-invite] result=$updateInviteResult")
      updateInviteResult.isRight === true
      val getUpdatedInviteResult = invitesService.get(createResult.value.id.get).await()
      getUpdatedInviteResult.isRight === true
      getUpdatedInviteResult.value.companyId === updatedInvite.companyId
    }

  }

  def createInvite(): XorType[Invite] = {
    val inviteData   = inviteDataArbitrary(roleId, companyId).arbitrary.sample.get
    val createResult = invitesService.create(inviteData, rootUser.id.get).await()
    println(s"[create-result] $createResult")
    createResult.isRight === true
    createResult
  }

  def createAdvertiserInvite(): XorType[Invite] = {
    val inviteData   = inviteAdvertiserDataArbitrary().arbitrary.sample.get
    val createResult = invitesService.createAdvertiserInvite(inviteData, rootUser.company.id.get, rootUser.id.get).await()
    println(s"[create-advertiser-invite-result] $createResult")
    createResult.isRight === true
    createResult
  }

  lazy val roleId: Long = {
    val roleIdResult = rolesServices.find(RolesConfiguration.CompanyManager.name).await()
    println(s"[role] $roleIdResult")
    roleIdResult.isRight === true
    roleIdResult.value.id.get
  }

  lazy val companyId: Long = {
    val companyIdResult = companiesService.create(testCompany).await()
    companyIdResult.value
  }
}
