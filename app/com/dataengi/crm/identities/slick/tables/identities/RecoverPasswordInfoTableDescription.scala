package com.dataengi.crm.identities.slick.tables.identities

import com.dataengi.crm.identities.models.{RecoverPasswordInfo, RecoverPasswordInfoStatuses}
import com.dataengi.crm.identities.models.RecoverPasswordInfoStatuses._
import com.dataengi.crm.identities.slick.tables.TableDescription

trait RecoverPasswordInfoTableDescription extends TableDescription {

  import profile.api._

  implicit val passwordInfoRecoverStatusMapper = MappedColumnType.base[RecoverPasswordInfoStatus, Int](
    { (value: RecoverPasswordInfoStatus) =>
      value.id
    }, { id: Int =>
      RecoverPasswordInfoStatuses(id)
    }
  )

  class RecoverPasswordInfoTable(tag: Tag) extends Table[RecoverPasswordInfo](tag, "recover_password_info") {

    def email       = column[String]("email")
    def host        = column[String]("host")
    def userId      = column[Long]("user_id")
    def expiredDate = column[Long]("expired_date")
    def recoverId   = column[String]("recover_id")
    def status      = column[RecoverPasswordInfoStatus]("status")
    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * =
      (email, host, userId, expiredDate, recoverId, status, id.?) <> (RecoverPasswordInfo.tupled, RecoverPasswordInfo.unapply)

  }

  val RecoverPasswordInfoTableQuery = TableQuery[RecoverPasswordInfoTable]

}
