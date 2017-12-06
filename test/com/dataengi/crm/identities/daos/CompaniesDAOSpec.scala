package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import com.dataengi.crm.identities.models.Company
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

/**
  * Created by nk91 on 06.12.16.
  */
class CompaniesDAOSpec extends PlaySpecification with AuthenticationContext with SpecificationsFinder {

  sequential

  override lazy val fakeModule = new FakeModule {
    additionalBindings = Seq(
      bind[CompaniesDAO].to[CompaniesSlickDAOImplementation]
    )
  }

  lazy val companiesDAO = application.injector.instanceOf[CompaniesDAO]

  lazy val TestCompany = Company("TestCompany")

  "CompaniesDAO" should {

    "add company and get by id" in {
      val addCompanyResult = companiesDAO.add(TestCompany).await()
      println(s"[companies-dao][add] $addCompanyResult}")
      addCompanyResult.isRight === true
      val id             = addCompanyResult.value
      val getByIdCompany = companiesDAO.get(id).await()
      getByIdCompany.isRight === true
      getByIdCompany.value === TestCompany.copy(id = Some(id))
    }

    "find exist company by name" in {
      val getByNameResult = companiesDAO.find(TestCompany.name).await()
      println(s"[companies-dao][get-by-name] $getByNameResult}")
      getByNameResult.isRight === true
    }

    "add and remove" in {
      val company          = Company("Company for removing")
      val addCompanyResult = companiesDAO.add(company).await()
      println(s"[companies-dao][add] $addCompanyResult}")
      addCompanyResult.isRight === true
      val id             = addCompanyResult.value
      val getByIdCompany = companiesDAO.get(id).await()
      getByIdCompany.isRight === true
      getByIdCompany.value === company.copy(id = Some(id))
      val removeCompanyResult = companiesDAO.delete(id).await()
      removeCompanyResult.isRight === true
    }

    "update company" in {
      val company             = Company("CompanyForUpdate")
      val companyId           = companiesDAO.add(company).await().value
      val updateForCompany    = Company("updatedcomany").copy(id = Some(companyId))
      val updateCompanyResult = companiesDAO.update(updateForCompany).await()
      updateCompanyResult.isRight === true
      val getByIdCompany = companiesDAO.get(companyId).await()
      getByIdCompany.isRight === true
      getByIdCompany.value.name === updateForCompany.name
    }

  }

}
