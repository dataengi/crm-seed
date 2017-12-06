package com.dataengi.crm.identities.models

case class Role(name: String, permissions: Seq[Permission], id: Option[Long] = None)
