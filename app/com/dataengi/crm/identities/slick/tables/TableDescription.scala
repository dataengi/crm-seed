package com.dataengi.crm.identities.slick.tables

import play.api.db.slick.HasDatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait TableDescription extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] {

  import profile.api._

  implicit val executionContext: ExecutionContext

  def enumColumnMapper[T <: Enumeration](enum: T): BaseColumnType[enum.Value] = MappedColumnType.base[enum.Value, String](
    v => v.toString,
    s => enum.withName(s)
  )

}
