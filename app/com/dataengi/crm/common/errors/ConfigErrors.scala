package com.dataengi.crm.common.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class ConfigError(description: String) extends AppErrorResult

object ConfigErrors {
  val ConfigNotFoundError = ConfigError("Config not found")

  def ConfigNotFoundError(name: String) = ConfigError(s"Cannot find config under key `$name`")
}