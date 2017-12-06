package com.dataengi.crm.contacts.controllers.data

import com.dataengi.crm.contacts.models.{ContactsBook, Group}

case class FullContactsBookData(ownerId: Long,
                                createDate: Long,
                                contacts: List[ContactData],
                                groups: List[Group],
                                id: Option[Long] = None)
object FullContactsBookData {

  def apply(contactsBook: ContactsBook, contacts: List[ContactData], groups: List[Group]): FullContactsBookData =
    new FullContactsBookData(
      ownerId = contactsBook.ownerId,
      createDate = contactsBook.createDate,
      contacts = contacts,
      groups = groups,
      id = contactsBook.id
    )

}
