package com.dataengi.crm.identities.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.errors.ValueNotFound
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.CompaniesServiceContext
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

/**
  * Created by nk91 on 25.11.16.
  */
class CompaniesServiceSpec extends PlaySpecification with CompaniesServiceContext with SpecificationsFinder {

  sequential

  "CompaniesService" should {

    "create company" in {
      val company             = companyArbitrary.arbitrary.sample.get
      val createCompanyResult = companiesService.create(company).await()
      createCompanyResult.isRight === true

      val getCompanyResult = companiesService.get(createCompanyResult.value).await()
      getCompanyResult.isRight === true
    }

    "get not exist company" in {
      val getCompanyResult = companiesService.get(Int.MaxValue).await()
      getCompanyResult.isLeft === true
    }

    "get all company" in {
      val allCompaniesResult = companiesService.all().await()
      allCompaniesResult.isRight === true
      allCompaniesResult.value.size must be_>(0)
    }

  }

}
