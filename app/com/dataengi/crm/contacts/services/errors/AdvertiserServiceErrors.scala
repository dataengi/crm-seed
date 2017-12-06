package com.dataengi.crm.contacts.services.errors

import com.dataengi.crm.common.context.types.AppErrorResult

case class AdvertiserServiceError(description: String) extends AppErrorResult

object AdvertiserServiceErrors {
  def AdvertiserWithUserIdNotExist(userId: Long) = AdvertiserServiceError(s"Advertiser with user id: $userId not exist")
}
