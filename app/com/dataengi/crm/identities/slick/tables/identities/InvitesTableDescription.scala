package com.dataengi.crm.identities.slick.tables.identities

import com.dataengi.crm.identities.models.InviteStatuses
import com.dataengi.crm.identities.models.InviteStatuses.InviteStatus
import com.dataengi.crm.identities.slick.tables.TableDescription

trait InvitesTableDescription extends TableDescription {

  import profile.api._

  implicit val inviteStatusMapper: BaseColumnType[InviteStatus] = enumColumnMapper(InviteStatuses)

  case class InviteRow(id: Long,
                       email: String,
                       roleId: Long,
                       companyId: Long,
                       expiredDate: Long,
                       status: InviteStatus,
                       hash: String,
                       invitedBy: Long)

  class InviteTable(tag: Tag) extends Table[InviteRow](tag, "invite") {
    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email       = column[String]("email")
    def roleId      = column[Long]("roleId")
    def companyId   = column[Long]("companyId")
    def expiredDate = column[Long]("expiredDate")
    def status      = column[InviteStatus]("status")
    def hash        = column[String]("hash")
    def invitedBy   = column[Long]("invitedBy")

    def * = (id, email, roleId, companyId, expiredDate, status, hash, invitedBy) <> (InviteRow.tupled, InviteRow.unapply)
  }

  val Invites = TableQuery[InviteTable]
}
