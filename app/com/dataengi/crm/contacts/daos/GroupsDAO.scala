package com.dataengi.crm.contacts.daos

import cats.instances.all._
import com.dataengi.crm.contacts.daos.errors.GroupsDAOErrors
import com.dataengi.crm.contacts.models.Group
import com.dataengi.crm.contacts.slick.tables.GroupTableDescription
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.daos.AutoIncBaseDAO
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

trait GroupsDAO extends AutoIncBaseDAO[Group] {

  def findByName(name: String): Or[Option[Group]]

  def get(ids: List[Long]): Or[List[Group]]

  def findByContactsBookId(contactsBookId: Long): Or[List[Group]]

}

@Singleton
class GroupsSlickDAOImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                             implicit val executionContext: ExecutionContext)
    extends GroupsDAO
    with GroupTableDescription
    with GroupsQueries {

  override def get(key: Long): Or[Group] = db.run(selectGroupAction(key)).toOrWithLeft(GroupsDAOErrors.GroupNotFound)

  override def getOption(key: Long): Or[Option[Group]] = db.run(selectGroupAction(key)).toOr

  override def add(obj: Group): Or[Long] = db.run(insertGroupAction(obj)).toOr

  override def add(values: List[Group]): Or[List[Long]] = db.run(insertGroupsAction(values).map(_.toList)).toOr

  override def update(obj: Group): Or[Unit] = obj.id match {
    case Some(id) => db.run(updateGroupAction(id, obj)).toEmptyOr
    case None     => GroupsDAOErrors.GroupIdIsEmpty.toErrorOr
  }

  override def delete(key: Long): Or[Unit] = db.run(deleteGroupAction(key)).toEmptyOr

  override def all: Or[List[Group]] = db.run(selectAllGroupsAction).toOr.map(_.toList)

  override def findByName(name: String): Or[Option[Group]] = db.run(selectByName(name)).toOr

  override def get(ids: List[Long]): Or[List[Group]] = db.run(selectGroupsAction(ids)).toOr.map(_.flatten)

  override def findByContactsBookId(contactsBookId: Long): Or[List[Group]] =
    db.run(selectGroupsByContactsBookId(contactsBookId)).toOr.map(_.toList)

}

trait GroupsQueries extends GroupTableDescription {

  import profile.api._

  val insertGroupQuery = Groups returning Groups.map(_.id)

  def insertGroupAction(group: Group) = insertGroupQuery += unMapGroup(group)

  def insertGroupsAction(groups: List[Group]) = insertGroupQuery ++= groups.map(unMapGroup)

  def updateGroupAction(id: Long, group: Group) =
    Groups.filter(_.id === id).update(GroupRow(id, group.name, group.contactsBookId, group.createDate))

  def deleteGroupAction(id: Long) = Groups.filter(_.id === id).delete

  def selectGroupAction(id: Long) = Groups.filter(_.id === id).result.headOption.map(_.map(mapGroup))

  def selectGroupsAction(ids: List[Long]) = DBIO.sequence(ids.map(selectGroupAction))

  def selectByName(name: String) = Groups.filter(_.name === name).result.headOption.map(_.map(mapGroup))

  def selectGroupsByContactsBookId(contactsBookId: Long) =
    Groups.filter(_.contactsBookId === contactsBookId).result.map(_.map(mapGroup))

  def selectAllGroupsAction = Groups.result.map(_.map(mapGroup))

  val mapGroup: (GroupRow) => Group = row => Group(row.name, row.contactsBookId, row.createDate, Some(row.id))

  val unMapGroup: (Group) => GroupRow = group =>
    GroupRow(group.id.getOrElse(0), group.name, group.contactsBookId, group.createDate)

}
