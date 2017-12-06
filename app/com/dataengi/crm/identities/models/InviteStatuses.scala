package com.dataengi.crm.identities.models

object InviteStatuses extends Enumeration {
  type InviteStatus = Value
  val Waiting, Registered, Expired = Value
}
