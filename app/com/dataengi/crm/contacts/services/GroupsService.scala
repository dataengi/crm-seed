package com.dataengi.crm.contacts.services

import cats.instances.all._
import com.dataengi.crm.contacts.controllers.data.{CreateGroupData, UpdateGroupData}
import com.dataengi.crm.contacts.models.Group
import com.dataengi.crm.contacts.repositories.GroupsRepository
import com.dataengi.crm.contacts.services.errors.GroupsServiceErrors
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext

trait GroupsService {

  def find(TestGroupName: String): Or[Option[Group]]

  def updateGroup(data: UpdateGroupData): EmptyOr

  def remove(id: Long): EmptyOr

  def get(ids: List[Long]): Or[List[Group]]

  def get(id: Long): Or[Group]

  def create(data: CreateGroupData): Or[Group]

  def findAllInContactsBook(contactsBookId: Long): Or[List[Group]]

}

@Singleton
class GroupsServiceImplementation @Inject()(groupsRepository: GroupsRepository,
                                            contactsBooksService: ContactsBooksService,
                                            contactsService: ContactsService,
                                            implicit val executionContext: ExecutionContext)
    extends GroupsService {

  override def get(ids: List[Long]): Or[List[Group]] = groupsRepository.get(ids).leftMap(GroupsServiceErrors.MapErrors)

  override def get(id: Long): Or[Group] = groupsRepository.get(id)

  override def create(data: CreateGroupData): Or[Group] =
    for {
      contactsBook <- contactsBooksService.get(data.contactsBookId)
      group = Group(data.name, data.contactsBookId, DateTime.now().getMillis)
      groupId <- groupsRepository.add(group)
    } yield group.copy(id = Some(groupId))

  override def findAllInContactsBook(contactsBookId: Long): Or[List[Group]] =
    groupsRepository.findAllInContactsBook(contactsBookId)

  override def remove(id: Long): EmptyOr = groupsRepository.remove(id)

  override def updateGroup(data: UpdateGroupData): EmptyOr =
    for {
      group        <- groupsRepository.get(data.groupId)
      updateResult <- groupsRepository.update(data.groupId, group.copy(name = data.name))
    } yield updateResult

  override def find(name: String): Or[Option[Group]] = groupsRepository.findByName(name)

}
