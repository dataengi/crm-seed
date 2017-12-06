package com.dataengi.crm.common.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class ValueNotFound(id: Long) extends AppErrorResult {

  override val description: String = s"Value with $id not found"

}
