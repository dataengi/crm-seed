package com.dataengi.crm.contacts.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.contacts.controllers.data.{AddContactsToGroupData, CreateGroupData}
import com.dataengi.crm.contacts.models.Group
import com.dataengi.crm.contacts.services.errors.GroupsServiceErrors
import com.dataengi.crm.contacts.context.GroupsServiceContext
import org.scalacheck.Gen
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

class GroupsServiceSpec extends PlaySpecification with GroupsServiceContext with SpecificationsFinder {

  sequential

  "GroupsService" should {

    "create group" in {
      val createContactsBook = contactsBooksService.create(rootUser.id.get).await()
      createContactsBook.isRight === true
      val contactsBook      = createContactsBook.value
      val createGroupData   = createGroupDataArbitrary.arbitrary.sample.get.copy(contactsBookId = contactsBook.id.get)
      val createGroupResult = groupsService.create(createGroupData).await()
      createGroupResult.isRight === true
      val getGroupResult = groupsService.get(createGroupResult.value.id.get).await()
      getGroupResult.isRight === true
      getGroupResult.value.name === createGroupData.name
    }

    "get all groups in contacts book" in {
      val createContactsBook = contactsBooksService.findByOwner(rootUser.id.get).await()
      createContactsBook.isRight === true
      val contactsBook       = createContactsBook.value
      val createContactsData = createGroupDataArbitrary.arbitrary.sample.get
      val createGroupInContactsBookResult =
        groupsService.create(createContactsData.copy(contactsBookId = contactsBook.id.get)).await()
      createGroupInContactsBookResult.isRight === true
      val allContactsInContactsBookResult = groupsService.findAllInContactsBook(contactsBook.id.get).await()
      allContactsInContactsBookResult.isRight === true
      allContactsInContactsBookResult.value.nonEmpty === true
    }

    "add contacts to group" in {
      val group              = createGroup(TestGroupName)
      val createContactsData = Gen.listOfN(5, createContactArbitrary.arbitrary.sample).sample.get.flatten
      val createContactsResult =
        createContactsData.traverseC(data => contactsService.create(data, rootUser.id.get)).await()
      createContactsResult.isRight === true
      val addToGropResult =
        contactsService
          .addGroup(AddContactsToGroupData(groupId = group.id.get, contactIds = createContactsResult.value.map(_.id.get)))
          .await()
      addToGropResult.isRight === true
    }

    "find group by name" in {
      val getTestGroup = groupsService.find(TestGroupName).toOrWithLeft(GroupsServiceErrors.GroupNotFoundError).await()
      getTestGroup.isRight === true
    }

    "get group members" in {
      val getTestGroup = groupsService.find(TestGroupName).toOrWithLeft(GroupsServiceErrors.GroupNotFoundError).await()
      getTestGroup.isRight === true
      val groupId            = getTestGroup.value.id.get
      val groupMembersResult = contactsService.getGroupMembers(groupId).await()
      groupMembersResult.isRight === true
      groupMembersResult.value.size === 5
    }

  }

  def createGroup(name: String): Group = {
    val createContactsBook = contactsBooksService.create(rootUser.id.get).await()
    createContactsBook.isRight === true
    val contactsBook      = createContactsBook.value
    val createGroupData   = CreateGroupData(name, contactsBookId = contactsBook.id.get)
    val createGroupResult = groupsService.create(createGroupData).await()
    createGroupResult.isRight === true
    createGroupResult.value
  }

}
