package com.dataengi.crm.profiles.slick

import com.dataengi.crm.identities.slick.tables.TablesInitiation
import com.dataengi.crm.profiles.slick.tables.ProfilesTableDescription
import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider

import scala.collection.immutable.List
import scala.concurrent.ExecutionContext

@Singleton
class InitiateProfilesTables @Inject()(val configuration: Configuration,
                                       protected val dbConfigProvider: DatabaseConfigProvider,
                                       implicit val executionContext: ExecutionContext)
    extends ProfilesTableDescription
    with TablesInitiation {

  val All = List(Profiles)
  createTables(All)
  printMigrationDDL(All)

}
