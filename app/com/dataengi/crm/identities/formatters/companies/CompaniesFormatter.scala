package com.dataengi.crm.identities.formatters.companies

import com.dataengi.crm.identities.controllers.data.CompanyData
import com.dataengi.crm.identities.models.Company
import play.api.libs.json.{JsValue, Json, OFormat}

trait CompaniesFormatter {

  implicit val companyFormatter: OFormat[Company] = Json.format[Company]

  implicit val companyDataFormatter: OFormat[CompanyData] = Json.format[CompanyData]

}

object CompaniesFormatter extends CompaniesFormatter
