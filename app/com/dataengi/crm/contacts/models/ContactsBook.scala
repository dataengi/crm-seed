package com.dataengi.crm.contacts.models

case class ContactsBook(ownerId: Long,
                        createDate: Long,
                        id: Option[Long] = None)
