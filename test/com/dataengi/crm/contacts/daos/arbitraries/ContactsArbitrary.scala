package com.dataengi.crm.contacts.daos.arbitraries

import com.dataengi.crm.contacts.controllers.data._
import com.dataengi.crm.contacts.models.{ContactFieldTypes, ContactTypes, Group}
import com.dataengi.crm.common.arbitraries.CommonArbitrary
import com.dataengi.crm.contacts.models.ContactFieldTypes.ContactFieldType
import com.dataengi.crm.contacts.models.ContactTypes.ContactType
import org.scalacheck.{Arbitrary, Gen}

trait ContactsArbitrary extends CommonArbitrary {

  val groupArbitrary: Arbitrary[Group]                              = Arbitrary(Gen.resultOf(Group))
  implicit val createGroupDataArbitrary: Arbitrary[CreateGroupData] = Arbitrary(Gen.resultOf(CreateGroupData))

  implicit val contactTypeArbitrary: Arbitrary[ContactType] = Arbitrary(Gen.oneOf(ContactTypes.values.toList))
  implicit val contactFieldTypeArbitrary: Arbitrary[ContactFieldType] = Arbitrary(
    Gen.oneOf(ContactFieldTypes.values.toList))
  implicit val addressDataArbitrary: Arbitrary[AddressData]         = Arbitrary(Gen.resultOf(AddressData))
  implicit val emailDataArbitrary: Arbitrary[EmailData]             = Arbitrary(Gen.resultOf(EmailData))
  implicit val phoneDataArbitrary: Arbitrary[PhoneData]             = Arbitrary(Gen.resultOf(PhoneData))
  implicit val createContactArbitrary: Arbitrary[CreateContactData] = Arbitrary(Gen.resultOf(CreateContactData))
  implicit val updateContactArbitrary: Arbitrary[UpdateContactData] = Arbitrary(Gen.resultOf(UpdateContactData))

}
