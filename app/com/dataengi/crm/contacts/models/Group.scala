package com.dataengi.crm.contacts.models

case class Group(name: String, contactsBookId: Long, createDate: Long, id: Option[Long] = None)
