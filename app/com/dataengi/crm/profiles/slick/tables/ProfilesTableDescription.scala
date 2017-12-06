package com.dataengi.crm.profiles.slick.tables

import com.dataengi.crm.identities.slick.tables.TableDescription

trait ProfilesTableDescription extends TableDescription {

  import profile.api._

  case class ProfileRow(userId: Long,
                        nickname: String,
                        email: String,
                        firstName: Option[String] = None,
                        lastName: Option[String] = None,
                        avatarUrl: Option[String] = None,
                        id: Long = 0)

  class ProfilesTable(tag: Tag) extends Table[ProfileRow](tag, "profiles") {

    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId    = column[Long]("user_id")
    def nickname  = column[String]("nickname")
    def email     = column[String]("email")
    def firstName = column[Option[String]]("first_name")
    def lastName  = column[Option[String]]("last_name")
    def avatarUrl = column[Option[String]]("avatar_url")

    def * = (userId, nickname, email, firstName, lastName, avatarUrl, id) <> (ProfileRow.tupled, ProfileRow.unapply)

  }

  lazy val Profiles = TableQuery[ProfilesTable]

}