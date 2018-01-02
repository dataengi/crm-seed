package com.dataengi.crm.identities.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import play.api.test.PlaySpecification

class UsersManagementServiceSpec extends PlaySpecification with AuthenticationContext {

  lazy val usersManagementService = application.injector.instanceOf[UsersManagementService]

  "UsersManagementService" should {

    "get company members" in {
      val companyMembersResult = usersManagementService.getCompanyMembers(rootUser.company.id.get, rootUser).await()
      companyMembersResult.isRight === true
      companyMembersResult.value.nonEmpty === true
    }

  }

}
