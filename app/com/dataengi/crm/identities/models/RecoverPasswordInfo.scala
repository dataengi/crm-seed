package com.dataengi.crm.identities.models

import com.dataengi.crm.identities.models.RecoverPasswordInfoStatuses.RecoverPasswordInfoStatus

case class RecoverPasswordInfo(email: String,
                               host: String,
                               userId: Long,
                               expiredDate: Long,
                               recoverId: String,
                               status: RecoverPasswordInfoStatus = RecoverPasswordInfoStatuses.WAITING,
                               id: Option[Long] = None)
