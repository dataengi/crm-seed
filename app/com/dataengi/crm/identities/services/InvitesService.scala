package com.dataengi.crm.identities.services

import java.util.UUID

import com.dataengi.crm.common.services.mailer.MailService
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.configurations.RolesConfiguration
import com.dataengi.crm.identities.controllers.data.{InviteAdvertiserData, InviteData}
import com.dataengi.crm.identities.errors.InvitesServiceErrors
import com.dataengi.crm.identities.models.{Invite, InviteStatuses, Role}
import com.dataengi.crm.identities.repositories.{InvitesRepository, RolesRepository}
import com.dataengi.crm.identities.utils.mails.MailsCreator
import com.google.inject.{Inject, Singleton}
import org.joda.time.DateTime
import cats.instances.all._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait InvitesService {

  def create(inviteData: InviteData, invitedByUserId: Long): Or[Invite]

  def createAdvertiserInvite(inviteData: InviteAdvertiserData, companyId: Long, invitedByUserId: Long): Or[Invite]

  def get(id: Long): Or[Invite]

  def getByHash(hash: UUID): Or[Invite]

  def getInviteUrl(invite: Invite): Or[String]

  def getByHash(hash: String): Or[Invite]

  def getByCompanyId(id: Long): Or[List[Invite]]

  def getByUserIdWhichInvite(id: Long): Or[List[Invite]]

  def update(id: Long, invite: Invite): EmptyOr

  def update(invite: Invite): EmptyOr

  def all(): Or[List[Invite]]

  def remove(id: Long): EmptyOr

}

@Singleton
class InvitesServiceImplementation @Inject()(invitesRepository: InvitesRepository,
                                             rolesRepository: RolesRepository,
                                             mailService: MailService,
                                             mailsCreator: MailsCreator,
                                             implicit val executionContext: ExecutionContext)
    extends InvitesService {

  override def create(inviteData: InviteData, invitedByUserId: Long): Or[Invite] =
    for {
      _    <- invitesRepository.checkInviteExist(inviteData.email)
      role <- rolesRepository.get(inviteData.roleId)
      invite = createInvite(role, inviteData.email, inviteData.companyId, invitedByUserId)
      key   <- invitesRepository.add(invite)
      email <- mailsCreator.signUpInviteMail(invite.hash)
      _     <- mailService.sendEmail(invite.email)(email)
    } yield {
      invite.copy(id = Some(key))
    }

  override def createAdvertiserInvite(inviteData: InviteAdvertiserData,
                                      companyId: Long,
                                      invitedByUserId: Long): Or[Invite] =
    for {
      _         <- invitesRepository.checkInviteExist(inviteData.email)
      maybeRole <- rolesRepository.find(RolesConfiguration.Advertiser.name)
      role      <- maybeRole.toOrWithLeftError(InvitesServiceErrors.CanNotFindAdvertiserRole)
      invite = createInvite(role, inviteData.email, companyId, invitedByUserId)
      key   <- invitesRepository.add(invite)
      email <- mailsCreator.signUpInviteMail(invite.hash)
      _     <- mailService.sendEmail(invite.email)(email)
    } yield {
      invite.copy(id = Some(key))
    }

  def createInvite(role: Role, email: String, companyId: Long, invitedByUserId: Long) =
    Invite(
      email = email,
      role = role,
      companyId = companyId,
      expiredDate = DateTime.now().plusDays(1).getMillis,
      status = InviteStatuses.Waiting,
      hash = UUID.randomUUID(),
      invitedByUserId
    )

  override def get(id: Long): Or[Invite] = {
    invitesRepository.get(id)
  }

  override def getByHash(hash: UUID): Or[Invite] = {
    invitesRepository.getByHash(hash)
  }

  override def getByCompanyId(id: Long): Or[List[Invite]] = {
    invitesRepository.getByCompanyId(id)
  }

  override def getByUserIdWhichInvite(id: Long): Or[List[Invite]] = {
    invitesRepository.getByInvitedBy(id)
  }

  override def update(id: Long, invite: Invite): EmptyOr = {
    invitesRepository.update(id, invite)
  }

  override def getByHash(hash: String): Or[Invite] =
    for {
      uuid   <- uuidFromString(hash)
      invite <- getByHash(uuid)
    } yield invite

  private def uuidFromString(uuid: String): Or[UUID] =
    Try {
      UUID.fromString(uuid).toOr
    }.recover {
      case error => InvitesServiceErrors.invalidUUIDError(uuid).toErrorOrWithType[UUID]
    }.get

  override def update(invite: Invite): EmptyOr = invite.id match {
    case Some(id) => invitesRepository.update(id, invite)
    case None     => InvitesServiceErrors.CanNotUpdateInviteWithoutId.toErrorOrWithType[Empty]
  }

  override def all(): Or[List[Invite]] = invitesRepository.getAll()

  // TODO: extract to config
  override def getInviteUrl(invite: Invite): Or[String] =
    s"/#/auth/signUp?email=${invite.email}&hash=${invite.hash}".toOr

  override def remove(id: Long): EmptyOr = invitesRepository.remove(id)

}
