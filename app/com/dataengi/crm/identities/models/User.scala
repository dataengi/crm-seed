package com.dataengi.crm.identities.models

import com.dataengi.crm.identities.models.UserStates.UserState
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import monocle.macros.GenLens
import monocle.{Lens, PLens}

case class User(loginInfo: LoginInfo,
                company: Company,
                role: Role,
                state: UserState = UserStates.Activated,
                id: Option[Long] = None)
    extends Identity

object UserStates extends Enumeration {

  type UserState = Value

  val Activated   = Value(0)
  val Deactivated = Value(1)

}

object Users {

  val role: Lens[User, Role]                                               = GenLens[User](_.role)
  val roleName: Lens[Role, String]                                         = GenLens[Role](_.name)
  val rolePermissions: Lens[Role, Seq[Permission]]                         = GenLens[Role](_.permissions)
  val userRoleName: PLens[User, User, String, String]                      = role composeLens roleName
  val userPermissions: PLens[User, User, Seq[Permission], Seq[Permission]] = role composeLens rolePermissions

}
