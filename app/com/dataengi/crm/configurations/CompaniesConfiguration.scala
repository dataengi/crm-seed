package com.dataengi.crm.configurations

import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import play.api.{Configuration, Logger}
import scalty.types._
import cats.instances.all._
import com.dataengi.crm.identities.services.CompaniesService

import scala.concurrent.ExecutionContext

@Singleton
class CompaniesConfiguration @Inject()(configuration: Configuration, companiesService: CompaniesService, implicit val executionContext: ExecutionContext) {

  def loadRootCompany: Unit = {
    val addRootCompanyOr = for {
      rootCompanyOpt <- companiesService.findOption(CompaniesConfiguration.RootCompanyName)
      rootCompanyId <- rootCompanyOpt match {
        case Some(company) =>
          Logger.info(s"[com.dataengi.crm.identities-initiation][load-root-company] $company already exists")
          company.id.get.toOr
        case None =>
          Logger.info(s"[com.dataengi.crm.identities-initiation][load-root-company] creating ${CompaniesConfiguration.RootCompanyName}")
          companiesService.create(CompaniesConfiguration.RootCompanyName)

      }
    } yield rootCompanyId

    val addRootCompanyResult = addRootCompanyOr.await()
    Logger.info(s"[com.dataengi.crm.identities-initiation][load-root-company] ${addRootCompanyResult.logResult}")
  }

}

object CompaniesConfiguration {
  val RootCompanyName = "ROOT_COMPANY"
}
