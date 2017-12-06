package com.dataengi.crm.identities.slick.tables.credentionals

import com.dataengi.crm.identities.slick.tables.TableDescription
import com.mohiva.play.silhouette.api.LoginInfo

trait LoginInfoTableDescription extends TableDescription {

  import profile.api._

  case class LoginInfoRow(providerID: String, providerKey: String, id: Long = 0L)

  class LoginInfoTable(tag: Tag) extends Table[LoginInfoRow](tag, "login_info") {
    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID  = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def *           = (providerID, providerKey, id) <> (LoginInfoRow.tupled, LoginInfoRow.unapply)
  }

  val LoginInfoTableQuery = TableQuery[LoginInfoTable]

  def fromLoginInfo(loginInfo: LoginInfo)  = LoginInfoRow(loginInfo.providerID, loginInfo.providerKey)
  def toLoginInfo(loginInfo: LoginInfoRow) = LoginInfo(loginInfo.providerID, loginInfo.providerKey)

}
