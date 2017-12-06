package com.dataengi.crm.identities.models

object Actions extends Enumeration(20) {
  type Action = Value
  val UsersManagement, CreateRootUser, CreateCompanyManagerUser, CompaniesManagement, CreateManagerUser, OrdersManagement,
  Reports, Deploying, Invoicing, Testing, InviteUser, CreateCompany, CompanyManagement = Value

}
