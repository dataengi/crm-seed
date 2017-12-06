package com.dataengi.crm.identities.models

case class IdentityInfo(user: User, companyMembers: List[Long], roles: List[Role])
