package com.dataengi.crm.contacts.controllers.swagger

import com.dataengi.crm.common.context.types.{PlayFailureResultResponse, PlaySuccessResultResponse}
import com.dataengi.crm.contacts.controllers.data.{CreateContactData, RemoveContactsData, UpdateContactData}
import io.swagger.annotations.{ApiOperation, ApiResponses, _}
import play.api.mvc.{Action, AnyContent}

trait ContactsApiSwaggerDescription {

  @ApiOperation(value = "Get all contacts", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[Array[Contact]], message = "List of all contacts"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def getContacts: Action[AnyContent]



  @ApiOperation(value = "Get current user contacts", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[List[Contact]], message = "Current user contacts"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def getCurrentUserContacts: Action[AnyContent]

  @ApiOperation(value = "ID of the contact", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[Contact], message = "Contact ID"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def get(id: Long): Action[AnyContent]

  @ApiOperation(value = "Create a  contact", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[Contact], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def create: Action[CreateContactData]

  @ApiOperation(value = "Update contact ID", httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[UpdateContactData], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def update(id: Int): Action[UpdateContactData]


  @ApiOperation(value = "Update advertiser ID", httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def updateAdvertiserId(id: Long, advId: Long): Action[AnyContent]

  @ApiOperation(value = "Remove contacts by IDs", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def removeContacts(): Action[RemoveContactsData]

  @ApiOperation(value = "Delete contact ID", httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def delete(id: Long): Action[AnyContent]


  @ApiOperation(value = " Delete advertiser ID", httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[PlaySuccessResultResponse], message = "Success"),
    new ApiResponse(code = 400, response = classOf[PlayFailureResultResponse], message = "Error")
  ))
  def deleteAdvertiserId(id: Long): Action[AnyContent]


}
