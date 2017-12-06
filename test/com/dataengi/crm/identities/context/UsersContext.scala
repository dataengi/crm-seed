package com.dataengi.crm.identities.context

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.dataengi.crm.common.context.CRMApplication
import com.dataengi.crm.configurations.{CompaniesConfiguration, RootConfiguration}
import com.dataengi.crm.identities.errors.UsersServiceErrors
import cats.instances.all._
import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.identities.daos.PasswordInfoDAO
import com.dataengi.crm.identities.models.User
import com.dataengi.crm.identities.services.{CompaniesService, RolesService, UsersService}

trait UsersContext extends CRMApplication {

  val DefaultUserPassword = "DefaultUserPassword"

  lazy val usersService: UsersService           = application.injector.instanceOf[UsersService]
  lazy val rolesService: RolesService           = application.injector.instanceOf[RolesService]
  lazy val companiesService: CompaniesService   = application.injector.instanceOf[CompaniesService]
  lazy val rootConfiguration: RootConfiguration = application.injector.instanceOf[RootConfiguration]
  lazy val passwordHasher: PasswordHasher       = application.injector.instanceOf[PasswordHasher]
  lazy val passwordInfoDAO: PasswordInfoDAO     = application.injector.instanceOf[PasswordInfoDAO]

  lazy val rootUser: User = {
    usersService
      .findByEmail(rootConfiguration.rootLoginInfo.providerKey)
      .toOrWithLeft(UsersServiceErrors.IdentityNotFound)
      .await()
      .value
  }

  def createUserWithRole(existRoleName: String, email: String, password: String = DefaultUserPassword) =
    for {
      role        <- rolesService.find(existRoleName)
      rootCompany <- companiesService.find(CompaniesConfiguration.RootCompanyName)
      loginInfo    = LoginInfo("credentials", email)
      passwordInfo = passwordHasher.hash(password)
      user         = User(company = rootCompany, loginInfo = loginInfo, role = role)
      userId                 <- usersService.save(user)
      savePasswordInfoResult <- passwordInfoDAO.add(loginInfo, passwordInfo).toOr
    } yield user.copy(id = Some(userId))

}
