package com.dataengi.crm.identities.daos.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class RecoverPasswordInfoDAOError(description: String) extends AppErrorResult

object RecoverPasswordInfoDAOErrors {

  val RecoverPasswordInfoNotFound = RecoverPasswordInfoDAOError("RecoverPasswordInfo not found in recover_password_info table")

}
