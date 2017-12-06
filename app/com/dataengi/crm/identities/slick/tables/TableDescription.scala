package com.dataengi.crm.identities.slick.tables

import play.api.db.slick.HasDatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait TableDescription extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] {

  implicit val executionContext: ExecutionContext

}