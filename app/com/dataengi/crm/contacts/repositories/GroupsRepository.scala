package com.dataengi.crm.contacts.repositories

import cats.instances.all._
import com.dataengi.crm.contacts.daos.GroupsDAO
import com.dataengi.crm.contacts.models.Group
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.repositories.{AutoIncRepository, BaseInMemoryRepository}
import cats.syntax.traverse._

import scala.concurrent.ExecutionContext

trait GroupsRepository extends AutoIncRepository[Group] {

  def findByName(name: String): Or[Option[Group]]

  def get(ids: List[Long]): Or[List[Group]]

  def findAllInContactsBook(contactsBookId: Long): Or[List[Group]]

}

@Singleton
class GroupsInMemoryRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext)
    extends BaseInMemoryRepository[Group]
    with GroupsRepository {

  override def beforeSave(key: Long, value: Group): Group = value.copy(id = Some(key))

  override def get(ids: List[Long]): Or[List[Group]] = ids.traverse(get)

  override def findAllInContactsBook(contactsBookId: Long): Or[List[Group]] =
    getAll().map(_.filter(_.contactsBookId == contactsBookId))

  override def findByName(name: String): Or[Option[Group]] = getAll().map(_.find(_.name == name))

}

@Singleton
class GroupsRepositoryImplementation @Inject()(implicit val executionContext: ExecutionContext, groupsDAO: GroupsDAO)
    extends GroupsRepository {

  override def findByName(name: String): Or[Option[Group]] = groupsDAO.findByName(name)

  override def get(ids: List[Long]): Or[List[Group]] = groupsDAO.get(ids)

  override def findAllInContactsBook(contactsBookId: Long): Or[List[Group]] = groupsDAO.findByContactsBookId(contactsBookId)

  override def getAll(): Or[List[Group]] = groupsDAO.all

  override def remove(id: Long): Or[Empty] = groupsDAO.delete(id)

  override def add(value: Group): Or[Long] = groupsDAO.add(value)

  override def add(values: List[Group]): Or[List[Long]] = groupsDAO.add(values)

  override def get(id: Long): Or[Group] = groupsDAO.get(id)

  override def update(id: Long, value: Group): EmptyOr = groupsDAO.update(value.copy(id = Some(id)))

  override def getOption(id: Long): Or[Option[Group]] = groupsDAO.getOption(id)

}
