package com.dataengi.crm.identities.daos

import java.util.UUID

import cats.instances.all._
import cats.syntax.all._
import com.dataengi.crm.common.daos.AutoIncBaseDAO
import com.dataengi.crm.identities.daos.errors.InvitesDAOErrors
import com.dataengi.crm.identities.models.{Invite, Role}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.slick.tables.identities.InvitesTableDescription
import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait InvitesDAO extends AutoIncBaseDAO[Invite] {

  def find(email: String): Or[Option[Invite]]

  def getByHash(hash: UUID): Or[Option[Invite]]

  def getByCompanyId(companyId: Long): Or[Seq[Invite]]

  def getByInvitedBy(userId: Long): Or[Seq[Invite]]

}

class InvitesSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                              implicit val executionContext: ExecutionContext)
    extends InvitesDAO
    with InvitesTableDescription
    with InvitesQueries {

  override def get(id: Long): Or[Invite] = db.run(selectInvite(id)).toOrWithLeft(InvitesDAOErrors.InviteNotFound)

  override def getOption(id: Long): Or[Option[Invite]] = db.run(selectInvite(id)).toOr

  override def add(invite: Invite): Or[Long] = db.run(insertInviteAction(invite)).toOr

  override def getByHash(hash: UUID): Or[Option[Invite]] = db.run(selectInviteByHash(hash.toString)).toOr

  override def getByCompanyId(companyId: Long): Or[Seq[Invite]] = db.run(selectInviteByCompanyID(companyId)).toOr

  override def getByInvitedBy(userId: Long): Or[Seq[Invite]] = db.run(selectInviteByInvitedBy(userId)).toOr

  override def update(invite: Invite): Or[Unit] = invite.id match {
    case Some(id) => db.run(updateInviteAction(id, invite)).toEmptyOr
    case None     => InvitesDAOErrors.InviteIdIsEmpty.toErrorOr
  }

  override def delete(id: Long): Or[Unit] = db.run(deleteInviteAction(id)).toEmptyOr

  override def find(email: String): Or[Option[Invite]] = db.run(selectInviteByEmail(email)).toOr

  override def add(values: List[Invite]): Or[List[Long]] = db.run(insertInvitesAction(values)).toOr.map(_.toList)

  override def all: Or[List[Invite]] = db.run(InviteAction).toOr
}

trait InvitesQueries extends InvitesTableDescription with RolesQueries {

  import profile.api._

  def insertInviteAction(invite: Invite) = Invites returning Invites.map(_.id) += unMapInvite(invite)

  def insertInvitesAction(invites: List[Invite]) = Invites returning Invites.map(_.id) ++= invites.map(unMapInvite)

  def updateInviteAction(id: Long, invite: Invite) = Invites.filter(_.id === id).update(unMapInvite(invite))

  def deleteInviteAction(id: Long) = Invites.filter(_.id === id).delete

  def selectInvite(id: Long) =
    (for {
      inviteOpt <- Invites.filter(_.id === id).result.headOption
      roleOpt   <- selectRoleOpt(inviteOpt)
    } yield cartesianResult(inviteOpt, roleOpt)).transactionally

  def selectInviteByEmail(email: String) =
    (for {
      inviteOpt <- Invites.filter(_.email === email).result.headOption
      roleOpt   <- selectRoleOpt(inviteOpt)
    } yield cartesianResult(inviteOpt, roleOpt)).transactionally

  def selectRoleOpt(inviteOpt: Option[InviteRow]) = inviteOpt match {
    case Some(invite) => selectRole(invite.roleId).map(_.headOption)
    case None         => DBIO.successful[Option[Role]](None)
  }

  def selectRoles(inviteOpt: Seq[InviteRow]) =
    DBIO.sequence(inviteOpt.map(raw => selectRole(raw.roleId).map(_.headOption)))

  def selectInviteByHash(hash: String) =
    (for {
      inviteOpt <- Invites.filter(_.hash === hash).result.headOption
      roleOpt   <- selectRoleOpt(inviteOpt)
    } yield cartesianResult(inviteOpt, roleOpt)).transactionally

  def cartesianResult(inviteOpt: Option[InviteRow], roleOpt: Option[Role]): Option[Invite] = {
//    (inviteOpt |@| roleOpt).map {
    (inviteOpt, roleOpt).mapN {
      case (invite, role) => mapInvite(invite, role)
    }
  }

  def selectInviteByCompanyID(companyId: Long) =
    (for {
      invites <- Invites.filter(_.companyId === companyId).result
      roles   <- selectRoles(invites)
    } yield zipSeqResult(invites, roles.flatten)).transactionally

  def selectInviteByInvitedBy(invitedBy: Long) =
    (for {
      invites <- Invites.filter(_.invitedBy === invitedBy).result
      roles   <- selectRoles(invites)
    } yield zipSeqResult(invites, roles.flatten)).transactionally

  def zipResult(inviteOpt: Option[InviteRow], roleOpt: Option[Role]): Seq[Invite] =
    (inviteOpt zip roleOpt).map {
      case (invite, role) => mapInvite(invite, role)
    }.toSeq

  def zipSeqResult(inviteOpt: Seq[InviteRow], roleOpt: Seq[Role]): Seq[Invite] =
    (inviteOpt zip roleOpt).map {
      case (invite, role) => mapInvite(invite, role)
    }

  def mapInvite(invite: InviteRow, role: Role): Invite = {
    Invite(
      email = invite.email,
      role = role,
      companyId = invite.companyId,
      expiredDate = invite.expiredDate,
      id = Some(invite.id),
      hash = UUID.fromString(invite.hash),
      invitedBy = invite.invitedBy,
      status = invite.status
    )
  }

  def InviteAction =
    (for {
      inviteOpt <- Invites.result
      roleOpt   <- selectRoles(inviteOpt)
    } yield
      inviteOpt
        .zip(roleOpt.flatten)
        .map {
          case (invite, role) => mapInvite(invite, role)
        }
        .toList).transactionally

  val unMapInvite: (Invite) => InviteRow = invite =>
    InviteRow(id = invite.id.getOrElse(0),
              email = invite.email,
              roleId = invite.role.id.getOrElse(0),
              companyId = invite.companyId,
              expiredDate = invite.expiredDate,
              status = invite.status,
              hash = invite.hash.toString,
              invitedBy = invite.invitedBy)

}
