package com.dataengi.crm.identities.init

import com.dataengi.crm.common.context.CRMApplication
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.configurations.CompaniesConfiguration
import com.dataengi.crm.identities.services.CompaniesService
import play.api.test.PlaySpecification

class IdentitiesInitiationSpec extends PlaySpecification with CRMApplication {

  lazy val companiesConfiguration = application.injector.instanceOf[CompaniesConfiguration]
  lazy val companiesService = application.injector.instanceOf[CompaniesService]

  "companiesConfiguration.loadRootCompany" should {
    "create root company at app first start" in {
      println("companiesConfiguration.loadRootCompany again")
      companiesConfiguration.loadRootCompany
      val rootCompany = companiesService.findOption(CompaniesConfiguration.RootCompanyName).await()
      println(s"rootCompany $rootCompany")
      rootCompany.isRight === true
    }
  }

}
