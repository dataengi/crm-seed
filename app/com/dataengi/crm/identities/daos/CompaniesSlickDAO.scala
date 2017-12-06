package com.dataengi.crm.identities.daos

import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.daos.AutoIncBaseDAO
import com.dataengi.crm.identities.models.Company
import play.api.db.slick.DatabaseConfigProvider
import com.dataengi.crm.identities.slick.tables.identities.CompaniesTableDescription
import cats.instances.all._
import com.dataengi.crm.identities.daos.errors.CompaniesDAOErrors

import scala.concurrent.ExecutionContext

trait CompaniesDAO extends AutoIncBaseDAO[Company] {

  def find(name: String): Or[Option[Company]]

}

@Singleton
class CompaniesSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                                implicit val executionContext: ExecutionContext)
  extends CompaniesDAO
    with CompaniesTableDescription
    with CompaniesQueries {

  override def get(id: Long): Or[Company] = db.run(selectCompany(id)).toOrWithLeft(CompaniesDAOErrors.CompanyNotFound)

  override def getOption(id: Long): Or[Option[Company]] = db.run(selectCompany(id)).toOr

  override def add(company: Company): Or[Long] = db.run(insertCompanyAction(company)).toOr

  override def update(company: Company): Or[Unit] = company.id match {
    case Some(id) => db.run(updateCompanyAction(id, company)).toEmptyOr
    case None     => CompaniesDAOErrors.CompanyIdIsEmpty.toErrorOr
  }

  override def delete(id: Long): Or[Unit] = db.run(deleteCompanyAction(id)).toEmptyOr

  override def find(name: String): Or[Option[Company]] = db.run(selectCompanyByName(name)).toOr

  override def add(values: List[Company]): Or[List[Long]] = db.run { insertCompaniesAction(values).map(_.toList) }.toOr

  override def all: Or[List[Company]] = db.run(companiesAction).toOr.map(_.toList)

}

trait CompaniesQueries extends CompaniesTableDescription {

  import profile.api._

  val insertCompanyQuery = Companies returning Companies.map(_.id)

  def insertCompanyAction(company: Company) = insertCompanyQuery += unMapCompany(company)

  def insertCompaniesAction(companies: List[Company]) =
    insertCompanyQuery ++= companies.map(unMapCompany)

  def updateCompanyAction(id: Long, company: Company) = Companies.filter(_.id === id).update(CompanyRow(company.name, id))

  def deleteCompanyAction(id: Long) = Companies.filter(_.id === id).delete

  def selectCompany(id: Long) = Companies.filter(_.id === id).result.headOption.map(_.map(mapCompany))

  def selectCompanyByName(name: String) = Companies.filter(_.name === name).result.headOption.map(_.map(mapCompany))

  def companiesAction = (Companies result).map(_.map(mapCompany))

  val mapCompany: (CompanyRow) => Company = raw => Company(raw.name, Some(raw.id))

  val unMapCompany: (Company) => CompanyRow = company => CompanyRow(company.name, company.id.getOrElse(0))

}

