package com.dataengi.crm.identities.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.errors.RolesServiceErrors
import com.dataengi.crm.identities.context.RolesServiceContext
import com.dataengi.crm.identities.models.PermissionStates.{apply => _}
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

/**
  * Created by nk91 on 25.11.16.
  */
class RolesServiceSpec extends PlaySpecification with RolesServiceContext with SpecificationsFinder {

  sequential

  "RolesService" should {

    "create role" in {
      val role           = roleArbitrary.arbitrary.sample.get
      val addRolesResult = rolesService.add(role).await()
      addRolesResult.isRight === true
    }

    "create role with the same name" in {
      val role           = roleArbitrary.arbitrary.sample.get
      val addRolesResult = rolesService.add(role).await()
      addRolesResult.isRight === true
      val roleWithSameName    = roleArbitrary.arbitrary.sample.get.copy(name = role.name)
      val addRoleWithSameName = rolesService.add(roleWithSameName).await()
      addRoleWithSameName.isLeft === true
      addRoleWithSameName.leftValue === RolesServiceErrors.RolesAlreadyExist
    }

    "root role must exist" in {
      val rootRoleResult = rolesService.findOption("ROOT_ROLE").await()
      rootRoleResult.isRight === true
    }

    "get all roles" in {
      val allRolesResult = rolesService.allRoles().await()
      allRolesResult.isRight === true
      allRolesResult.value.size must be_>(0)
    }

  }

}
