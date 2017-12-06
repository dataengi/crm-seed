package com.dataengi.crm.identities.slick.tables.identities

import com.dataengi.crm.identities.slick.tables.TableDescription

trait CompaniesTableDescription extends TableDescription {

  import profile.api._

  case class CompanyRow(name: String, id: Long = 0l)

  class CompaniesTable(tag: Tag) extends Table[CompanyRow](tag, "companies") {

    def id   = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (name, id) <> (CompanyRow.tupled, CompanyRow.unapply)

  }

  val Companies = TableQuery[CompaniesTable]

}
