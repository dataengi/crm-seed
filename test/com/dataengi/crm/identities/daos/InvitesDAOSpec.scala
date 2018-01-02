package com.dataengi.crm.identities.daos

import java.util.{Calendar, UUID}

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import com.dataengi.crm.identities.models._
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

class InvitesDAOSpec extends PlaySpecification with AuthenticationContext {

  sequential

  lazy val invitesDAO = application.injector.instanceOf[InvitesDAO]
  lazy val rolesDAO   = application.injector.instanceOf[RolesDAO]
  lazy val TestRole   = Role("TestRole", Seq(Permission(Actions.UsersManagement, PermissionStates.Allow)))

  var TestInvite = Invite("test@gmail.com",
                               TestRole,
                               Calendar.getInstance.getTimeInMillis,
                               Calendar.getInstance.getTimeInMillis,
                               InviteStatuses.Waiting,
                               UUID.randomUUID(),
                               Calendar.getInstance.getTimeInMillis)

  "InivitesDAO" should {

    "add invite and get by id" in {
      val addTestRoleResult = rolesDAO.add(TestRole).await()
      addTestRoleResult.isRight == true
      TestInvite = TestInvite.copy(role = TestRole.copy(id = Some(addTestRoleResult.value)))
      val addInviteResult = invitesDAO.add(TestInvite).await()
      addInviteResult.isRight === true
      val id        = addInviteResult.value
      val allResult = invitesDAO.all.await()
      allResult.isRight === true
      val getByIdInvite = invitesDAO.get(id).await()
      getByIdInvite.isRight === true
      getByIdInvite.value.email === TestInvite.copy(id = Some(id)).email
    }

    "find exist company by email" in {
      val getByEmailResult = invitesDAO.find(TestInvite.email).await()
      getByEmailResult.isRight === true
    }

    "add and remove" in {
      val invite          = TestInvite.copy(email = "testForRemove@gmail.com")
      val addInviteResult = invitesDAO.add(invite).await()
      addInviteResult.isRight === true
      val id            = addInviteResult.value
      val getByIdInvite = invitesDAO.get(id).await()
      getByIdInvite.isRight === true
      getByIdInvite.value.email === invite.copy(id = Some(id)).email
      val removeInviteResult = invitesDAO.delete(id).await()
      removeInviteResult.isRight === true
    }

    "update company" in {
      val invite             = TestInvite.copy(email = "forUpdate1@gmail.com")
      val inviteId           = invitesDAO.add(invite).await().value
      val updateForInvite    = TestInvite.copy(email = "forUpdate2@gmail.com").copy(id = Some(inviteId))
      val updateInviteResult = invitesDAO.update(updateForInvite).await()
      updateInviteResult.isRight === true
      val getByIdInvite = invitesDAO.get(inviteId).await()
      getByIdInvite.isRight === true
      getByIdInvite.value.email === updateForInvite.email
    }

  }

}
