package com.dataengi.crm.identities.slick.initiation

import com.dataengi.crm.identities.slick.tables.{AllIdentitiesTablesDescriptions, TablesInitiation}
import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

@Singleton
class InitiateTables @Inject()(val configuration: Configuration,
                               protected val dbConfigProvider: DatabaseConfigProvider,
                               implicit val executionContext: ExecutionContext)
    extends AllIdentitiesTablesDescriptions
    with TablesInitiation {

  createTables(AllIdentitiesTables)
  printMigrationDDL(AllIdentitiesTables)

}
