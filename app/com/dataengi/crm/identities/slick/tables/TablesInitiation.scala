package com.dataengi.crm.identities.slick.tables

import java.io.{File, FileWriter}

import play.api.{Configuration, Logger}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.lifted.TableQuery
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._

import scala.concurrent.ExecutionContext

trait TablesInitiation extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] {

  implicit val executionContext: ExecutionContext

  val configuration: Configuration

  val DDLTag: String = super.getClass.getSimpleName

  def printMigrationDDL(tables: List[slick.lifted.TableQuery[_ <: slick.relational.RelationalProfile#Table[_]]]) = {
    val allowPrintDDL = configuration.getOptional[Boolean]("play.db.ddl.print").getOrElse(false)
    Logger.info(s"[initiate-table][print-ddl] allowPrintDDL=$allowPrintDDL")
    if (allowPrintDDL) {
      createDDL(tables, DDLTag)
    }
  }

  private def createDDL(tables: List[TableQuery[_ <: slick.relational.RelationalProfile#Table[_]]], DDLPath: String) = {
    import profile.api._
    val schema    = tables.map(_.schema).reduce(_ ++ _)
    val directory = new File(s"./db/statements/${DDLPath}/")
    if (!directory.exists()) directory.mkdirs()
    val migrationFile = new File(directory.getPath + "/migration_ddl.sql")
    val writer        = new FileWriter(migrationFile.getAbsoluteFile)
    writer.write("# --- !Ups\n\n")
    schema.createStatements.foreach { s =>
      writer.write(s + ";\n")
    }
    writer.write("\n\n# --- !Downs\n\n")
    schema.dropStatements.foreach { s =>
      writer.write(s + ";\n")
    }
    writer.close()
  }

  def createTables(tables: List[slick.lifted.TableQuery[_ <: slick.relational.RelationalProfile#Table[_]]]) = {
    import profile.api._
    val allowCreateTables     = configuration.getOptional[Boolean]("play.db.create.dynamic").getOrElse(false)
    val printStatementsTables = configuration.getOptional[Boolean]("play.db.print.statements").getOrElse(false)
    Logger.info(s"[initiate-table][create] allowCreateTables=$allowCreateTables")
    if (allowCreateTables) {
      val schema             = tables.map(_.schema).reduce(_ ++ _)
      val schemaCreateResult = db.run(schema.create).toOr.await()
      if (printStatementsTables) Logger.info(s"[initiate-table][${DDLTag}] create query: ${schema.create.statements}")
      if (!List("already exist", "not found").exists(schemaCreateResult.logResult.contains)) {
        Logger.info(s"[initiate-table][${DDLTag}] create tables: ${schemaCreateResult.logResult}")
      }
    }
  }

}
