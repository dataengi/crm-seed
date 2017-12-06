package com.dataengi.crm.identities.slick.tables.credentionals

import com.dataengi.crm.identities.slick.tables.TableDescription

trait PasswordInfoTableDescription extends TableDescription {

  import profile.api._

  case class PasswordInfoRaw(hasher: String, password: String, salt: Option[String], id: Long)

  class PasswordInfoTable(tag: Tag) extends Table[PasswordInfoRaw](tag, "password_info") {
    def hasher      = column[String]("hasher")
    def password    = column[String]("password")
    def salt        = column[Option[String]]("salt")
    def loginInfoId = column[Long]("login_info_id")
    def *           = (hasher, password, salt, loginInfoId) <> (PasswordInfoRaw.tupled, PasswordInfoRaw.unapply)
  }

  val PasswordInfoTableQuery = TableQuery[PasswordInfoTable]

}
