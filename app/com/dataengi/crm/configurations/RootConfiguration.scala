package com.dataengi.crm.configurations

import com.dataengi.crm.identities.models.{Company, Role, User}
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.{PasswordHasher}
import play.api.Configuration

@Singleton
class RootConfiguration @Inject()(configuration: Configuration, passwordHasher: PasswordHasher) {

  private val rootEmail = configuration.getOptional[String]("crm.users.root.email").getOrElse("admin")
  val rootPassword      = configuration.getOptional[String]("crm.users.root.password").getOrElse("admin")
  val rootLoginInfo     = LoginInfo("credentials", rootEmail)
  val rootPasswordInfo  = passwordHasher.hash(rootPassword)

  def createRootUser(rootCompany: Company, rootRole: Role): User = User(
    loginInfo = rootLoginInfo,
    role = rootRole,
    company = rootCompany
  )

}
