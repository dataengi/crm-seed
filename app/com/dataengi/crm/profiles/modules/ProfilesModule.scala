package com.dataengi.crm.profiles.modules

import com.dataengi.crm.profiles.daos.{ProfilesDAO, ProfilesSlickDAOImplementation}
import com.dataengi.crm.profiles.repositories.{ProfilesRepository, ProfilesRepositoryImplementation}
import com.dataengi.crm.profiles.services._
import com.dataengi.crm.profiles.slick.InitiateProfilesTables
import net.codingwell.scalaguice.ScalaModule

class ProfilesModule extends ScalaModule {

  override def configure() = {

    bind[InitiateProfilesTables].asEagerSingleton()

    bind[ProfilesService].to[ProfilesServiceImplementation]
    bind[AvatarService].to[AvatarServiceImplementation]
    bind[GravatarServiceSettings].toInstance(GravatarServiceSettings())

    bind[ProfilesRepository].to[ProfilesRepositoryImplementation]

    bind[ProfilesDAO].to[ProfilesSlickDAOImplementation]
  }

}
