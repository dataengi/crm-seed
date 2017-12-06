package com.dataengi.crm.contacts.controllers.swagger

import com.dataengi.crm.common.context.types.{PlayFailureResultResponse, PlaySuccessResultResponse}
import com.dataengi.crm.contacts.controllers.data.{AddContactsToGroupData, CreateGroupData, LeaveGroupData, UpdateGroupData}
import com.dataengi.crm.contacts.models.Group
import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import play.api.mvc.{Action, AnyContent}

trait ContactsGroupApiSwaggerDescription {

  @ApiOperation(value = "Get all groups", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[Group], message = "List of all groups"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def getGroup(id: Long): Action[AnyContent]





  @ApiOperation(value = "Create new group", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[CreateGroupData], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def createGroup: Action[CreateGroupData]


  @ApiOperation(value = "Add new contact to group", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[AddContactsToGroupData], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def addContactToGroup(): Action[AddContactsToGroupData]



  @ApiOperation(value = "Remove contact from group", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[LeaveGroupData], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def removeContactFromGroup(): Action[LeaveGroupData]



  @ApiOperation(value = "Update group data", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def updateGroup(): Action[UpdateGroupData]



  @ApiOperation(value = "Delete group", httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def deleteGroup(id: Long): Action[AnyContent]

  @ApiOperation(value = "Get all groups in contacts book", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[List[Group]], message = "List of all groups in contacts book"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def allGroupInContactsBook(contactsBookId: Long): Action[AnyContent]

}
