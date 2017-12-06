package com.dataengi.crm.identities.slick.tables

import com.dataengi.crm.identities.slick.tables.credentionals.{LoginInfoTableDescription, PasswordInfoTableDescription}
import com.dataengi.crm.identities.slick.tables.identities._

trait AllIdentitiesTablesDescriptions
    extends LoginInfoTableDescription
    with PasswordInfoTableDescription
    with UsersTableDescription
    with CompaniesTableDescription
    with RolesTableDescription
    with PermissionsTableDescription
    with InvitesTableDescription
    with JWTAuthenticatorTableDescription
    with RecoverPasswordInfoTableDescription {

  val AllIdentitiesTables =
    List(LoginInfoTableQuery,
         PasswordInfoTableQuery,
         RecoverPasswordInfoTableQuery,
         Users,
         Companies,
         Permissions,
         Roles,
         Invites,
         JWTAuthenticators)

}
