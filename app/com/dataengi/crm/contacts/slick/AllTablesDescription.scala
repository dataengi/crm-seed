package com.dataengi.crm.contacts.slick

import com.dataengi.crm.contacts.slick.tables._

trait AllTablesDescription
    extends AddressTableDescription
    with ContactsBookTableDescription
    with ContactsTableDescription
    with EmailTableDescription
    with GroupTableDescription
    with GroupToContactsTableDescription
    with PhoneTableDescription {

  val All = List(Addresses,
                 ContactsBooks,
                 Contacts,
                 Phones,
                 Emails,
                 Groups,
                 GroupToContacts)
}
