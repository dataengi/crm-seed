package com.dataengi.crm.identities.errors

import com.dataengi.crm.common.context.types.AppErrorResult
import com.dataengi.crm.identities.models.Company

object CompanyServiceErrors {

  def companyWithNameNotFound(name: String) = CompanyServiceError(s"Company with name $name not found")

  def companyAlreadyExist(id: Long): CompanyServiceError = CompanyServiceError(s"Company with $id already exist")

  def companyAlreadyExist(company: Company): CompanyServiceError = companyAlreadyExist(company.id.get)

}

case class CompanyServiceError(description: String) extends AppErrorResult
