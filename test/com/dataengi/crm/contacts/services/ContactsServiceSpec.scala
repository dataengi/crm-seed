package com.dataengi.crm.contacts.services

import com.dataengi.crm.common.context.types._
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.contacts.models.Contact
import com.dataengi.crm.contacts.context.ContactsServiceContext
import com.mohiva.play.silhouette.api.LoginInfo
import com.dataengi.crm.configurations.{CompaniesConfiguration, RolesConfiguration}
import com.dataengi.crm.identities.models.{Company, Role, User}
import org.specs2.runner.SpecificationsFinder
import play.api.test.PlaySpecification

class ContactsServiceSpec extends PlaySpecification with ContactsServiceContext {

  sequential

  "ContactsService" should {

    "create contact" in {
      val createResult     = createTestContact()
      val getContactResult = contactsService.get(createResult.value.id.get).await()
      getContactResult.isRight === true
      ignoreIds(createResult.value) === ignoreIds(getContactResult.value)
    }

    "get not exists contact" in {
      val getContactResult = contactsService.get(Int.MaxValue).await()
      getContactResult.isLeft === true
      val getOptionContactResult = contactsService.getOption(Int.MaxValue).await()
      getOptionContactResult.isRight === true
      getOptionContactResult.value.isEmpty === true
    }

    "remove contact" in {
      val createResult     = createTestContact()
      val getContactResult = contactsService.get(createResult.value.id.get).await()
      getContactResult.isRight === true
      val deleteResult = contactsService.remove(createResult.value.id.get).await()
      deleteResult.isRight === true
      val getRemovedContactResult = contactsService.get(createResult.value.id.get).await()
      getRemovedContactResult.isLeft === true
    }

    "update contact in exist contacts book and the same groups" in {
      val createResult     = createTestContact()
      val getContactResult = contactsService.get(createResult.value.id.get).await()
      getContactResult.isRight === true
      val existContact: Contact = getContactResult.value
      val updatedContact = updateContactArbitrary.arbitrary.sample.get
        .copy(id = existContact.id.get, contactsBookId = existContact.contactsBookId, groupIds = existContact.groupIds)
      val updatedContactResult = contactsService.update(updatedContact, rootUser.id.get).await()
      updatedContactResult.isRight === true
      val getUpdatedContactResult = contactsService.get(updatedContactResult.value.id.get).await()
      getUpdatedContactResult.isRight === true
    }

    "get all contacts" in {
      val getContactsResult = contactsService.getContacts().await()
      getContactsResult.isRight === true
      getContactsResult.value.size must be_>(0)
    }

    "get contacts of a particular user" in {
      val rootCompanyOr = companiesService.findOption(CompaniesConfiguration.RootCompanyName).await()
      rootCompanyOr.isRight === true
      val rootCompany = rootCompanyOr.value.get

      val companyManagerRoleOr = rolesService.findOption(RolesConfiguration.CompanyManager.name).await()
      companyManagerRoleOr.isRight === true
      val companyManagerRole = companyManagerRoleOr.value.get

      val firstUserId = createUser("some@some.com", rootCompany, companyManagerRole)
      createTestContact(firstUserId)
      createTestContact(firstUserId)

      val secondUserId = createUser("other@some.com", rootCompany, companyManagerRole)
      createTestContact(secondUserId)
      createTestContact(secondUserId)

      val allContactsOr = contactsService.getContacts().await()
      allContactsOr.isRight === true
      val allContacts = allContactsOr.value
      allContacts.length must be_>=(4)

      val firstUserContacts: List[Contact] = contactsService.getUserContacts(firstUserId).await().value
      firstUserContacts.length === 2
      val secondUserContacts: List[Contact] = contactsService.getUserContacts(secondUserId).await().value
      secondUserContacts.length === 2
    }

    "add advertiserId to contact" in {
      val AdvertiserId     = 2L
      val createResult     = createTestContact()
      val getContactResult = contactsService.get(createResult.value.id.get).await()
      getContactResult.isRight === true
      ignoreIds(createResult.value) === ignoreIds(getContactResult.value)

      val setAdvertiserIdResult = contactsService.updateAdvertiserId(createResult.value.id.get, AdvertiserId).await()
      setAdvertiserIdResult.isRight === true
      val getContactWithAdvertiserIdResult = contactsService.get(createResult.value.id.get).await()
      getContactWithAdvertiserIdResult.isRight === true
      getContactWithAdvertiserIdResult.value.advertiserId.isDefined === true

      val deleteAdvertiserIdResult = contactsService.deleteAdvertiserId(createResult.value.id.get).await()
      deleteAdvertiserIdResult.isRight === true
      val getContactWitoutAdvertiserIdResult = contactsService.get(createResult.value.id.get).await()
      getContactWitoutAdvertiserIdResult.isRight === true
      getContactWitoutAdvertiserIdResult.value.advertiserId.isEmpty === true
    }

  }

  def createUser(email: String, rootCompany: Company, companyManagerRole: Role): Long = {
    val userIdOr = usersService.save(User(LoginInfo("credentials", email), rootCompany, companyManagerRole)).await()
    userIdOr.isRight === true
    userIdOr.value
  }

  def createTestContact(userId: Long = rootUser.id.get): XorType[Contact] = {
    val createContactData = createContactArbitrary.arbitrary.sample.get
    val createResult      = contactsService.create(createContactData, userId).await()
    createResult.isRight === true
    createResult
  }

}
