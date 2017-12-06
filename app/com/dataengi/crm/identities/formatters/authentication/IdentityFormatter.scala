package com.dataengi.crm.identities.formatters.authentication

import com.dataengi.crm.common.extensions.formatters.JsonFormatter
import com.dataengi.crm.common.extensions.formatters.JsonFormatterExtension._
import com.dataengi.crm.identities.formatters.companies.CompaniesFormatter._
import com.dataengi.crm.identities.formatters.roles.RoleFormatter
import com.dataengi.crm.identities.models.UserStates.UserState
import com.dataengi.crm.identities.models.{IdentityInfo, User, UserStates}
import play.api.libs.json._

trait IdentityFormatter extends RoleFormatter {
  implicit val userStateFormatter: Format[UserState] = JsonFormatter.enumFormat(UserStates)

  implicit val identityFormatter: OFormat[User] = Json.format[User]

  implicit val customIdentityWrites: Writes[User] = new Writes[User] {

    override def writes(o: User): JsValue = Json.obj(
      "email"   -> o.loginInfo.providerKey,
      "company" -> o.company.toJson(companyFormatter),
      "role"    -> o.role.toJson(roleFormatter),
      "state"   -> o.state.toJson(userStateFormatter),
      "id"      -> o.id
    )

  }

  implicit val identityInfoFormatter: OFormat[IdentityInfo] = Json.format[IdentityInfo]
}

object IdentityFormatter extends IdentityFormatter

