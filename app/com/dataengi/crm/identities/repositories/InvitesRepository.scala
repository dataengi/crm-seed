package com.dataengi.crm.identities.repositories

import java.util.UUID

import com.google.inject.Inject
import errors.InvitesRepositoryErrors
import com.google.inject.Singleton
import scalty.types.or
import cats.instances.all._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.InvitesDAO
import com.dataengi.crm.identities.models.Invite

import scala.concurrent.{ExecutionContext, Future}

trait InvitesRepository extends AutoIncRepository[Invite] {

  def getByHash(hash: UUID): Or[Invite]

  def getByCompanyId(companyId: Long): Or[List[Invite]]

  def getByInvitedBy(userId: Long): Or[List[Invite]]

  def checkInviteExist(email: String): EmptyOr

}

@Singleton
class InvitesInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[Invite]
    with InvitesRepository {

  override protected def beforeSave(key: Long, value: Invite) = value.copy(id = Some(key))

  override def getByHash(hash: UUID): Or[Invite] = {
    Future
      .successful(repository.values.find(_.hash == hash))
      .toOrWithLeft(InvitesRepositoryErrors.InviteWithHashNotExist(hash))
  }

  override def getByCompanyId(companyId: Long): Or[List[Invite]] = {
    Future.successful(repository.values.filter(_.companyId == companyId).toList.reverse).toOr
  }

  override def getByInvitedBy(userId: Long): Or[List[Invite]] = {
    Future.successful(repository.values.filter(_.invitedBy == userId).toList.reverse).toOr
  }

  override def checkInviteExist(email: String): EmptyOr = {
    repository.values.find(_.email == email).toOr.flatMap {
      case Some(_) => InvitesRepositoryErrors.InviteAlreadyExist.toErrorOr
      case None    => or.EMPTY_OR
    }
  }

}

@Singleton
class InvitesRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext, invitesDAO: InvitesDAO)
    extends InvitesRepository {

  override def getByHash(hash: UUID): Or[Invite] =
    invitesDAO.getByHash(hash).toOrWithLeft(InvitesRepositoryErrors.InviteWithHashNotExist(hash))

  override def getByCompanyId(companyId: Long): Or[List[Invite]] = invitesDAO.getByCompanyId(companyId).map(_.toList)

  override def getByInvitedBy(userId: Long): Or[List[Invite]] = invitesDAO.getByInvitedBy(userId).map(_.toList)

  override def checkInviteExist(email: String): EmptyOr = invitesDAO.find(email).flatMap {
    case Some(_) => InvitesRepositoryErrors.InviteAlreadyExist.toErrorOr
    case None    => or.EMPTY_OR
  }

  override def getAll(): Or[List[Invite]] = invitesDAO.all

  override def remove(id: Long): Or[Empty] = invitesDAO.delete(id)

  override def add(value: Invite): Or[Long] = invitesDAO.add(value)

  override def add(values: List[Invite]): Or[List[Long]] = invitesDAO.add(values)

  override def get(id: Long): Or[Invite] = invitesDAO.get(id)

  override def update(id: Long, value: Invite): EmptyOr = invitesDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[Invite]] = invitesDAO.getOption(id)

}
