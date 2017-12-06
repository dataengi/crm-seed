package com.dataengi.crm.identities.models

object RecoverPasswordInfoStatuses extends Enumeration {
  type RecoverPasswordInfoStatus = Value
  val WAITING, RECOVERED = Value
}
