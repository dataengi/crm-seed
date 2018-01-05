package com.dataengi.crm.identities.daos

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.context.AuthenticationContext
import com.dataengi.crm.identities.daos.errors.{CompaniesDAOError, CompaniesDAOErrors}
import com.dataengi.crm.identities.models.Company
import play.api.test.PlaySpecification

class CompaniesDAOSpec extends PlaySpecification with AuthenticationContext {

  sequential

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
      getByIdCompany.value === Company("TestCompany", Some(id))

    }

    "Select company option by id " in {
      val addCompanyResult = companiesDAO.add(TestCompany).await()
      addCompanyResult.isRight === true
      val id             = addCompanyResult.value
      val getByIdCompany = companiesDAO.getOption(id).await()
      getByIdCompany.isRight === true
      getByIdCompany.value === Some(TestCompany.copy(id = Some(id)))

    }

    "Update company " in {
      val addCompanyResult: XorType[Long] = companiesDAO.add(TestCompany).await()
      addCompanyResult.isRight === true
      val id: Long       = addCompanyResult.value
      val getByIdCompany = companiesDAO.get(id).await()
      getByIdCompany.isRight === true
      getByIdCompany.value === TestCompany.copy(id = Some(id))
      val updateCompany = companiesDAO.update(getByIdCompany.right.get.copy(name = "another name")).await()
      updateCompany.isRight === true
      val updatedCompany = companiesDAO.get(id).await()
      updatedCompany.value === Company("another name", Some(id))

    }

    "get company by id - negative" in {
      val result = companiesDAO.clear.await()
      result.isRight === true
      val getByIdCompany = companiesDAO.get(0).await()

      getByIdCompany.isLeft === true

      getByIdCompany must beLeft(CompaniesDAOErrors.CompanyNotFound)

    }

    "update negative" in {

      val result = companiesDAO.update(Company("test")).await()

      result.isLeft === true

      result must beLeft(CompaniesDAOErrors.CompanyIdIsEmpty)
    }

    "Delete company by id " in {
      val addCompanyResult = companiesDAO.add(TestCompany).await()
      addCompanyResult.isRight === true
      val id             = addCompanyResult.value
      val getByIdCompany = companiesDAO.get(id).await()
      getByIdCompany.isRight === true
      getByIdCompany.value === TestCompany.copy(id = Some(id))
      val deleteCompanyResult = companiesDAO.delete(id).await()
      deleteCompanyResult.isRight === true
    }

    "find exist company by name" in {
      val getByNameResult = companiesDAO.find(TestCompany.name).await()
      println(s"[companies-dao][get-by-name] $getByNameResult}")
      getByNameResult.isRight === true
    }

    "Add list of companies" in {
      val company1         = Company(name = "Test")
      val company2         = Company(name = "Test1")
      val companies      = List(company1, company2)
      val addCompanyResult = companiesDAO.add(companies).await()
      println(s"[companies-dao][add] $addCompanyResult}")
      val companiesDB = addCompanyResult.value

      companiesDB must have size 2

      val id1            = addCompanyResult.right.get.head
      val id2            = addCompanyResult.right.get.last
      val getByCompanyId = companiesDAO.get(id1).await()
      getByCompanyId.isRight === true
      val getByIdCompany = companiesDAO.get(id2).await()
      getByIdCompany.isRight === true

    }

    "list of companies" in {
      val result = companiesDAO.clear.await()
      result.isRight === true
      val getByIdCompany = companiesDAO.all.await()
      getByIdCompany.isRight === true
      getByIdCompany.value === List()
    }

    "list of companies - positive" in {
      val company1         = Company(name = "Test")
      val company2         = Company(name = "Test1")
      val CompanyList      = List(company1, company2)
      val addCompanyResult = companiesDAO.add(CompanyList).await()
      println(s"[companies-dao][add] $addCompanyResult}")
      addCompanyResult.isRight === true
      val getByIdCompany = companiesDAO.all.await()
      getByIdCompany.isRight === true

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
      val updateForCompany    = Company("updatedcompany", id = Some(companyId))
      val updateCompanyResult = companiesDAO.update(updateForCompany).await()
      updateCompanyResult.isRight === true
      val getByIdCompany = companiesDAO.get(companyId).await()
      getByIdCompany.isRight === true
      getByIdCompany.value.name === updateForCompany.name
    }

  }

}
