package com.dataengi.crm.contacts.slick

import com.dataengi.crm.identities.slick.tables.TablesInitiation
import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

@Singleton
class InitiateContactsTables @Inject()(val configuration: Configuration,
                                       protected val dbConfigProvider: DatabaseConfigProvider,
                                       implicit val executionContext: ExecutionContext)
    extends AllTablesDescription
    with TablesInitiation {

  createTables(All)
  printMigrationDDL(All)

}
