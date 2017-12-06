package com.dataengi.crm.identities.repositories

import cats.instances.all._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.daos.RolesDAO
import com.dataengi.crm.identities.models.Role
import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

trait RolesRepository extends AutoIncRepository[Role] {

  def clearAllPermissions(): EmptyOr

  def find(name: String): Or[Option[Role]]
}

@Singleton
class RolesInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[Role]
    with RolesRepository {

  override def beforeSave(key: Long, value: Role): Role = value.copy(id = Some(key))

  override def find(name: String): Or[Option[Role]] = getAll().map(_.find(_.name == name))

  override def clearAllPermissions(): EmptyOr = getAll().map(_.map(role => update(role.id.get, role.copy(permissions = Seq()))))
}

@Singleton
class RolesRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext, rolesDAO: RolesDAO)
    extends RolesRepository {

  override def find(name: String): Or[Option[Role]] = rolesDAO.find(name)

  override def getAll(): Or[List[Role]] = rolesDAO.all

  override def remove(id: Long): Or[Empty] = rolesDAO.delete(id)

  override def add(role: Role): Or[Long] = rolesDAO.add(role)

  override def add(roles: List[Role]): Or[List[Long]] = rolesDAO.add(roles)

  override def get(id: Long): Or[Role] = rolesDAO.get(id)

  override def update(id: Long, role: Role): EmptyOr = rolesDAO.update(role.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[Role]] = rolesDAO.getOption(id)

  override def clearAllPermissions(): EmptyOr = rolesDAO.clearAllPermissions()
}
