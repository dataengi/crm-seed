package com.dataengi.crm.identities.modules

import cats.instances.all._
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.extensions.logging._
import com.dataengi.crm.configurations.{CompaniesConfiguration, RolesConfiguration, RootConfiguration}
import com.google.inject.{Inject, Singleton}
import play.api.Logger
import com.dataengi.crm.identities.daos.PasswordInfoDAO
import com.dataengi.crm.identities.services.{CompaniesService, RolesService}
import com.dataengi.crm.identities.modules.InitiationErrors._
import com.dataengi.crm.identities.repositories.UsersRepository

import scala.concurrent.ExecutionContext

@Singleton
class IdentitiesInitiation @Inject()(usersRepository: UsersRepository,
                                     passwordInfoDAO: PasswordInfoDAO,
                                     companiesService: CompaniesService,
                                     rolesService: RolesService,
                                     rolesConfiguration: RolesConfiguration,
                                     companiesConfiguration: CompaniesConfiguration,
                                     rootConfiguration: RootConfiguration,
                                     implicit val executionContext: ExecutionContext) {

  companiesConfiguration.loadRootCompany
  rolesConfiguration.loadBaseRoles

  val addRootUserResult: Or[Long] = for {
    rootCompany <- companiesService.findOption(CompaniesConfiguration.RootCompanyName).toOrWithLeft(RootCompanyNotFound)
    rootRole    <- rolesService.findOption(RolesConfiguration.Root).toOrWithLeft(RootRoleNotFound)
    rootUserOpt <- usersRepository.find(rootConfiguration.rootLoginInfo)
    saveRootUserResult <- rootUserOpt match {
      case Some(user) =>
        Logger.info(s"[com.dataengi.crm.identities-initiation][add-root] updating root user ${rootConfiguration.rootLoginInfo}")
        usersRepository
          .update(user.id.get, rootConfiguration.createRootUser(rootCompany, rootRole))
          .map(result => user.id.get)
      case None =>
        Logger.info(s"[com.dataengi.crm.identities-initiation][add-root] creating root user ${rootConfiguration.rootLoginInfo}")
        usersRepository.add(rootConfiguration.createRootUser(rootCompany, rootRole))
    }
    savePasswordInfoResult <- passwordInfoDAO.add(rootConfiguration.rootLoginInfo, rootConfiguration.rootPasswordInfo).toOr
  } yield saveRootUserResult

  val result = addRootUserResult.await()

  Logger.info(s"[com.dataengi.crm.identities-initiation][add-root] ${result.logResult}")

}
