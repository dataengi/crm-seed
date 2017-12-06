package com.dataengi.crm.contacts.controllers

import javax.inject._

import com.dataengi.crm.identities.actions.SecuredFilteredAction
import cats.instances.all._
import com.dataengi.crm.contacts.controllers.data.{ContactData, FullContactsBookData}
import com.dataengi.crm.contacts.formatters.ContactFormatter
import com.dataengi.crm.contacts.services.ContactsBooksService
import com.mohiva.play.silhouette.api.Silhouette
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.contacts.formatters.ContactFormatter._
import com.dataengi.crm.contacts.services.{ContactsService, GroupsService}
import cats.syntax.traverse._
import com.dataengi.crm.common.controllers.ApplicationController
import com.dataengi.crm.contacts.controllers.swagger.{ContactsApiSwaggerDescription, ContactsBookApiSwaggerDescription, ContactsGroupApiSwaggerDescription}
import play.api.mvc._
import com.dataengi.crm.identities.utils.auth.DefaultEnv
import io.swagger.annotations.{ApiParam, _}

import scala.concurrent.ExecutionContext

@Api("Contacts")
@Singleton
class ContactsBookManagementController @Inject()(contactsService: ContactsService,
                                                 contactsBooksService: ContactsBooksService,
                                                 groupsService: GroupsService,
                                                 components: ControllerComponents,
                                                 val silhouette: Silhouette[DefaultEnv],
                                                 implicit val executionContext: ExecutionContext)
    extends ApplicationController(components)
      with ContactsApiSwaggerDescription
      with ContactsBookApiSwaggerDescription
      with ContactsGroupApiSwaggerDescription
    with SecuredFilteredAction {

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def getContacts = SecuredAccessAction.async { implicit request =>
    contactsService.getContacts()

  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def getCurrentUserContacts = SecuredAccessAction.async { implicit request =>
    contactsService.getUserContacts(request.identity.id.get)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def get(@ApiParam(value = "ID of the contact") id: Long) = SecuredAccessAction.async { implicit request =>
    contactsService.get(id)
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.CreateContactData",
      required = true,
      paramType = "body"
    )))
  def create = SecuredAccessAction.async(parse.json(ContactFormatter.createContactDataFormatter)) { implicit request =>
    contactsService.create(request.body, request.identity.id.get)
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.UpdateContactData",
      required = true,
      paramType = "body"
    )))
  def update(@ApiParam(value = "Update ID of the contact") id: Int) = SecuredAccessAction.async(parse.json(ContactFormatter.updateContactDataFormatter)) { implicit request =>
    contactsService.update(request.body, request.identity.id.get)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def delete(@ApiParam(value = "Delete contact ID")id: Long) = SecuredAccessAction.async { implicit request =>
    contactsService.remove(id.toLong)
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.CreateGroupData",
      required = true,
      paramType = "body"
    )))
  def createGroup = SecuredAccessAction.async(parse.json(ContactFormatter.createGroupFormatter)) { implicit request =>
    groupsService.create(request.body)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def allGroupInContactsBook(@ApiParam(value = "Get all group in contact book")contactsBookId: Long) = SecuredAccessAction.async { implicit request =>
    groupsService.findAllInContactsBook(contactsBookId)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def getGroup(@ApiParam(value = "List of groups") id: Long) = SecuredAccessAction.async { implicit request =>
    groupsService.get(id)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def getContactsBook = SecuredAccessAction.async { implicit request =>
    val fullContactsBook = contactsBookByOwner(request.identity.id.get)
    fullContactsBook
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.GetContactsBooksData",
      required = true,
      paramType = "body"
    )))
  def getContactsBooks = SecuredAccessAction.async(parse.json(ContactFormatter.getContactsBooksDataFormatter)) { implicit request =>
    request.body.ownerIds.traverse(contactsBookByOwner)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def getContactsBookByOwner(@ApiParam(value = "Contact book by owner")userId: Long) = SecuredAccessAction.async { implicit request =>
    contactsBookByOwner(userId)
  }

  private def contactsBookByOwner(userId: Long): Or[FullContactsBookData] =
    for {
      contactsBook <- contactsBooksService.findByOwner(userId)
      groups       <- groupsService.findAllInContactsBook(contactsBook.id.get)
      contacts     <- contactsService.findAllFromContactsBook(contactsBook.id.get)
      contactsData <- contacts.traverseC(contact => groupsService.get(contact.groupIds).map(ContactData(contact, _)))
    } yield FullContactsBookData(contactsBook, contactsData, groups)

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.AddContactsToGroupData",
      required = true,
      paramType = "body"
    )))
  def addContactToGroup = SecuredAccessAction.async(parse.json(ContactFormatter.addContactsToGroupDataFormatter)) { implicit request =>
    contactsService.addGroup(request.body)
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.RemoveContactsFromGroupData",
      required = true,
      paramType = "body"
    )))
  def removeContactFromGroup = SecuredAccessAction.async(parse.json(ContactFormatter.leaveGroupDataFormatter)) { implicit request =>
    contactsService.removeContactFromGroup(request.body)
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.UpdateGroupData",
      required = true,
      paramType = "body"
    )))
  def updateGroup = SecuredAccessAction.async(parse.json(ContactFormatter.updateGroupDataFormatter)) { implicit request =>
    groupsService.updateGroup(request.body)
  }

  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header"),
    new ApiImplicitParam(
      name = "body",
      dataType = "com.dataengi.contacts.controllers.data.RemoveContactsData",
      required = true,
      paramType = "body"
    )))
  def removeContacts = SecuredAccessAction.async(parse.json(ContactFormatter.removeContactsDataFormatter)) { implicit request =>
    contactsService.removeContacts(request.body)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def deleteGroup(@ApiParam(value = "Delete group")id: Long) = SecuredAccessAction.async { implicit request =>
    groupsService.remove(id)
  }


  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def updateAdvertiserId(@ApiParam(value = "Advertiser ID") id: Long,
                         @ApiParam(value = "Advertiser ID") advId: Long) = SecuredAccessAction.async { implicit request =>
    contactsService.updateAdvertiserId(id, advId)
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "X-Auth-Token", required = true, dataType = "string", paramType = "header")))
  def deleteAdvertiserId(@ApiParam(value = "Delete advertiser ID")id: Long) = SecuredAccessAction.async { implicit request =>
    contactsService.deleteAdvertiserId(id)
  }

}
