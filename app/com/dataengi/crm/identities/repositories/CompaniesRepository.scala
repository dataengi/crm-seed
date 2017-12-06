package com.dataengi.crm.identities.repositories

import com.google.inject.Inject
import com.google.inject.Singleton

import scala.concurrent.ExecutionContext
import cats.instances.all._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.CompaniesDAO
import com.dataengi.crm.identities.models.Company

trait CompaniesRepository extends AutoIncRepository[Company] {

  def find(name: String): Or[Option[Company]]

}

@Singleton
class CompaniesInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[Company]
    with CompaniesRepository {

  override protected def beforeSave(key: Long, value: Company): Company = value.copy(id = Some(key.toLong))

  override def find(name: String): Or[Option[Company]] =
    getAll().map(companies => companies.find(company => company.name == name))
}

@Singleton
class CompaniesRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext,
                                                  companiesDAO: CompaniesDAO)
    extends CompaniesRepository {

  override def find(name: String): Or[Option[Company]] = companiesDAO.find(name)

  override def get(key: Long): Or[Company] = companiesDAO.get(key)

  override def getOption(key: Long): Or[Option[Company]] = companiesDAO.getOption(key)

  override def add(company: Company): Or[Long] = companiesDAO.add(company)

  override def getAll(): Or[List[Company]] = companiesDAO.all

  override def remove(id: Long): Or[Empty] = companiesDAO.delete(id)

  override def add(values: List[Company]): Or[List[Long]] = companiesDAO.add(values)

  override def update(id: Long, value: Company): Or[Empty] = companiesDAO.update(value)

}
