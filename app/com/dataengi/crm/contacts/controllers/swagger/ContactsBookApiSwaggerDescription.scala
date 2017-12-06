package com.dataengi.crm.contacts.controllers.swagger

import com.dataengi.crm.contacts.controllers.data.{FullContactsBookData, GetContactsBooksData}
import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import play.api.mvc.{Action, AnyContent}
import scalty.results.ErrorResult

trait ContactsBookApiSwaggerDescription {

  @ApiOperation(value = "Contact book", httpMethod = "GET")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[FullContactsBookData], message = "List of contact book"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def getContactsBook: Action[AnyContent]

  @ApiOperation(value = "Contact books", httpMethod = "POST")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[FullContactsBookData], message = "List of contact book"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def getContactsBooks: Action[GetContactsBooksData]

  @ApiOperation(value = "Contact book by owner", httpMethod = "GET")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, response = classOf[FullContactsBookData], message = "List of contact book by owner"),
      new ApiResponse(code = 400, response = classOf[ErrorResult], message = "Error")
    ))
  def getContactsBookByOwner(userId: Long): Action[AnyContent]

}
