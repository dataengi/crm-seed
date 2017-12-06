package com.dataengi.crm.identities.services

import com.dataengi.crm.identities.repositories.CompaniesRepository
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.controllers.data.CompanyData
import com.dataengi.crm.identities.errors.CompanyServiceErrors
import com.dataengi.crm.identities.models.Company
import com.google.inject.{Inject, Singleton}
import cats.instances.all._

import scala.concurrent.ExecutionContext

trait CompaniesService {

  def create(name: String): Or[Long]

  def create(company: Company): Or[Long]

  def create(companyData: CompanyData): Or[Company]

  def get(id: Long): Or[Company]

  def findOption(name: String): Or[Option[Company]]

  def find(name: String): Or[Company]

  def all(): Or[List[Company]]

}

@Singleton
class CompaniesServiceImplementation @Inject()(companiesRepository: CompaniesRepository,
                                               implicit val executionContext: ExecutionContext)
    extends CompaniesService {

  override def create(name: String): Or[Long] = companiesRepository.find(name).flatMap {
    case Some(company) => CompanyServiceErrors.companyAlreadyExist(company).toErrorOr
    case None          => companiesRepository.add(Company(name))
  }

  override def create(company: Company): Or[Long] = create(company.name)

  override def get(id: Long): Or[Company] = companiesRepository.get(id)

  override def all(): Or[List[Company]] = companiesRepository.getAll()

  override def findOption(name: String): Or[Option[Company]] = companiesRepository.find(name)

  override def find(name: String): Or[Company] =
    findOption(name).toOrWithLeft(CompanyServiceErrors.companyWithNameNotFound(name))

  override def create(companyData: CompanyData): Or[Company] =
    for {
      companyId <- create(companyData.name)
      company   <- get(companyId)
    } yield company

}
