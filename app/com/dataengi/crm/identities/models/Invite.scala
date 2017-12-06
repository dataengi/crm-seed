package com.dataengi.crm.identities.models

import java.util.UUID

import com.dataengi.crm.identities.models.InviteStatuses.InviteStatus

case class Invite(email: String,
                  role: Role,
                  companyId: Long,
                  expiredDate: Long,
                  status: InviteStatus,
                  hash: UUID,
                  invitedBy: Long,
                  id: Option[Long] = None)
