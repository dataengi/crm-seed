package com.dataengi.crm.identities.slick.tables.identities

import com.dataengi.crm.identities.models.JWTAuthenticatorData
import com.dataengi.crm.identities.slick.tables.TableDescription

trait JWTAuthenticatorTableDescription extends TableDescription {

  import profile.api._

  class JWTAuthenticatorTable(tag: Tag) extends Table[JWTAuthenticatorData](tag, "jwt_authenticators") {

    def id            = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def identifier    = column[String]("identifier")
    def authenticator = column[String]("authenticator")

    def * = (authenticator, identifier, id) <> (JWTAuthenticatorData.tupled, JWTAuthenticatorData.unapply)

  }

  val JWTAuthenticators = TableQuery[JWTAuthenticatorTable]

}
