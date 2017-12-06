package com.dataengi.crm.identities.actions

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.identities.actions.errors.ActionErrors
import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext
import cats.instances.all._
import com.dataengi.crm.identities.models.Actions
import com.dataengi.crm.identities.models.Actions.Action

trait ActionsProvider {

  def actions: Or[List[Action]]

  def find(action: String): Or[Action]

}

@Singleton
class ActionsProviderImplementation @Inject()(implicit val executionContext: ExecutionContext) extends ActionsProvider {

  override def actions: Or[List[Action]] = Actions.values.toList.toOr

  override def find(action: String): Or[Action] =
    Actions.values.find(_.toString.equalsIgnoreCase(action)).toOrWithLeftError(ActionErrors.actionNotFound(action))
}
