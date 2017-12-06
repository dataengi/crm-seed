package com.dataengi.crm.common.errors

import com.dataengi.crm.common.context.types.AppErrorResult
import play.api.data.FormError

case class FormValidationError(errors: Seq[FormError]) extends AppErrorResult {

  override val description: String = errors.toString()

}
