package com.dataengi.crm.configurations

import com.dataengi.crm.identities.actions.ActionsProvider
import cats.instances.all._
import com.google.inject.{Inject, Singleton}
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import play.api.{Configuration, Logger}
import com.dataengi.crm.identities.models.{Actions, Permissions, Role}
import com.dataengi.crm.identities.services.RolesService

import scala.concurrent.ExecutionContext

@Singleton
class RolesConfiguration @Inject()(configuration: Configuration,
                                   actionsProvider: ActionsProvider,
                                   rolesService: RolesService,
                                   implicit val executionContext: ExecutionContext) {

  val baseRoles: List[Role] = List(
    RolesConfiguration.Invoicing,
    RolesConfiguration.Deploying,
    RolesConfiguration.Testing,
    RolesConfiguration.SalesRepresentative,
    RolesConfiguration.CompanyManager,
    RolesConfiguration.Manager,
    RolesConfiguration.Advertiser
  )

  def loadBaseRoles: Empty = {
    val loadBaseRoles = for {
      clearPermissions <- rolesService.clearAllPermissions()
      permissions      <- actionsProvider.actions.map(_.map(Permissions.allowed).toList)
      rootRole     = Role(RolesConfiguration.Root, permissions)
      updatedRoles = baseRoles :+ rootRole
      roleIds <- {
        updatedRoles.traverseC { updatedRole =>
          for {
            roleOpt <- rolesService.findOption(updatedRole.name)
            role <- roleOpt match {
              case Some(existingRole) =>
                val id: Long = existingRole.id.get
                Logger.info(
                  s"[com.dataengi.crm.identities-initiation][load-base-roles] Role $updatedRole being updated by id $id")
                rolesService.update(id, updatedRole).map(result => id)

              case None =>
                Logger.info(s"[com.dataengi.crm.identities-initiation][load-base-roles] Role $updatedRole being created")
                rolesService.add(updatedRole)
            }
          } yield role
        }
      }
    } yield roleIds

    val loadBaseRoleResult = loadBaseRoles.await()

    Logger.info(s"[com.dataengi.crm.identities-initiation][load-base-roles] ${loadBaseRoleResult.logResult}")

  }

}

object RolesConfiguration {

  val Root = "ROOT"

  val CompanyManager = Role(
    name = "COMPANY_MANAGER",
    permissions = (Actions.values -- Set(Actions.CreateRootUser, Actions.CompaniesManagement, Actions.CreateCompany))
      .map(Permissions.allowed)
      .toList
  )

  val Manager = Role(
    name = "MANAGER",
    permissions = (Actions.values -- Set(Actions.CreateRootUser,
                                         Actions.CompaniesManagement,
                                         Actions.CreateCompanyManagerUser,
                                         Actions.CreateCompany)).map(Permissions.allowed).toList
  )

  val SalesRepresentative = Role(
    name = "SALES_REPRESENTATIVE",
    permissions = Seq(Permissions.allowed(Actions.OrdersManagement))
  )

  val Testing = Role(
    name = "TESTING_REPRESENTATIVE",
    permissions = Seq(Permissions.allowed(Actions.Testing))
  )

  val Deploying = Role(
    name = "DEPLOYING_REPRESENTATIVE",
    permissions = Seq(Permissions.allowed(Actions.Deploying))
  )

  val Invoicing = Role(
    name = "INVOICING_REPRESENTATIVE",
    permissions = Seq(Permissions.allowed(Actions.Invoicing))
  )

  val ManagerLimitRoles = Set(
    Root,
    CompanyManager.name
  )

  val Advertiser = Role(
    name = "ADVERTISER",
    permissions = Seq(Permissions.allowed(Actions.OrdersManagement))
  )

}
