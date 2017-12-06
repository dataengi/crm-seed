package com.dataengi.crm.identities.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class CompaniesDAOError(description: String) extends AppErrorResult

object CompaniesDAOErrors {

  val CompanyNotFound  = CompaniesDAOError("Company not found in companies table")
  val CompanyIdIsEmpty = CompaniesDAOError("Can not do action because company doesn't exist")

}
