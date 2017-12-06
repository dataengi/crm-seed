package com.dataengi.crm.contacts.modules

import com.dataengi.crm.contacts.daos._
import com.dataengi.crm.contacts.repositories._
import com.dataengi.crm.contacts.services._
import com.dataengi.crm.contacts.slick.InitiateContactsTables
import net.codingwell.scalaguice.ScalaModule

class ContactsModule extends ScalaModule {

  override def configure() = {

    // Init
    bind[InitiateContactsTables].asEagerSingleton()

    // Services
    bind[ContactsService].to[ContactsServiceImplementation]
    bind[ContactsBooksService].to[ContactsBooksServiceImplementation]
    bind[GroupsService].to[GroupsServiceImplementation]

    // Repositories
    bind[ContactsRepository].to[ContactsRepositoryImplementation]
    bind[ContactsBooksRepository].to[ContactsBooksRepositoryImplementation]
    bind[GroupsRepository].to[GroupsRepositoryImplementation]

    // DAOs
    bind[GroupsDAO].to[GroupsSlickDAOImplementation]
    bind[ContactsSlickDAO].to[ContactsSlickDAOImplementation]
    bind[ContactsBooksDAO].to[ContactsBooksSlickDAOImplementation]

  }

}
