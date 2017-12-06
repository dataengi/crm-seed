package com.dataengi.crm.identities.modules

import com.dataengi.crm.configurations.{CompaniesConfiguration, RolesConfiguration, RootConfiguration, SilhouetteConfiguration}
import com.dataengi.crm.identities.actions.{ActionsProvider, ActionsProviderImplementation}
import com.google.inject.name.Named
import com.google.inject.{Provides, Singleton}
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import com.mohiva.play.silhouette.api.crypto.{AuthenticatorEncoder, Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.services._
import net.ceedubs.ficus.readers.EnumerationReader._
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import com.dataengi.crm.identities.daos._
import com.dataengi.crm.identities.repositories._
import com.dataengi.crm.identities.services._
import com.dataengi.crm.identities.slick.initiation.InitiateTables
import com.dataengi.crm.identities.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

class SilhouetteModule extends ScalaModule {

  def configure() = {
    // Initiation ORDER make sense
    bind[InitiateTables].asEagerSingleton()
    bind[IdentitiesInitiation].asEagerSingleton()

    // Silhouette
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())

    // Replace this with the bindings to your concrete DAOs
    bind[PasswordInfoDAO].to[PasswordInfoDAOSlickImplementation]
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO]

    // Repository
    bind[RolesRepository].to[RolesRepositoryImplementation]
    bind[CompaniesRepository].to[CompaniesRepositoryImplementation]
    bind[InvitesRepository].to[InvitesRepositoryImplementation]
    bind[JWTAuthenticatorRepository].to[JWTAuthenticatorCacheRepositoryImplementation]
    bind[UsersRepository].to[UsersRepositoryImplementation]
    bind[RecoverPasswordInfoRepository].to[RecoverPasswordInfoRepositoryImplementation]

    // DAOs
    bind[CompaniesDAO].to[CompaniesSlickDAOImplementation]
    bind[RolesDAO].to[RolesSlickDAOImplementation]
    bind[InvitesDAO].to[InvitesSlickDAOImplementation]
    bind[JWTAuthenticatorDAO].to[JWTAuthenticatorSlickDAOImplementation]
    bind[UsersDAO].to[UsersSlickDAOImplementation]
    bind[RecoverPasswordInfoDAO].to[RecoverPasswordInfoSlickDAOImplementation]

    // Services
    bind[UsersService].to[UsersServiceImplementation]
    bind[CompaniesService].to[CompaniesServiceImplementation]
    bind[RolesService].to[RolesServiceImplementation]
    bind[InvitesService].to[InvitesServiceImplementation]
    bind[AuthenticationService].to[AuthenticationServiceImplementation]
    bind[UsersManagementService].to[UsersManagementServiceImplementation]

    // Actions
    bind[ActionsProvider].to[ActionsProviderImplementation]

    // Configuration
    bind[RolesConfiguration].asEagerSingleton()
    bind[CompaniesConfiguration].asEagerSingleton()
    bind[RootConfiguration].asEagerSingleton()
    bind[SilhouetteConfiguration].asEagerSingleton()

  }

  /**
    * Provides the HTTP layer implementation.
    *
    * @param client Play's WS client.
    * @return The HTTP layer implementation.
    */
  @Provides
  def provideHTTPLayer(client: WSClient, executionContext: ExecutionContext): HTTPLayer = new PlayHTTPLayer(client)(executionContext)

  @Provides
  def provideSecureRandomIDGenerator(executionContext: ExecutionContext): IDGenerator = new SecureRandomIDGenerator()(executionContext)

  /**
    * Provides the Silhouette environment.
    *
    * @param userService The user service implementation.
    * @param authenticatorService The authentication service implementation.
    * @param eventBus The event bus instance.
    * @return The Silhouette environment.
    */
  @Provides
  def provideEnvironment(userService: UsersService,
                         authenticatorService: AuthenticatorService[JWTAuthenticator],
                         eventBus: EventBus,
                         executionContext: ExecutionContext): Environment[DefaultEnv] = {
    Environment[DefaultEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )(executionContext)
  }

  /**
    * Provides the crypter for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The crypter for the authenticator.
    */
  @Singleton
  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")

    new JcaCrypter(config)
  }

  /**
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo], executionContext: ExecutionContext): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO)(executionContext)
  }

  /**
    * Provide AuthenticatorEncoder
    * @param crypter The crypter implementation.
    * @return
    */
  @Singleton
  @Provides
  def provideAuthenticatorEncoder(@Named("authenticator-crypter") crypter: Crypter): AuthenticatorEncoder = {
    new CrypterAuthenticatorEncoder(crypter)
  }

  @Provides
  def provideJWTAuthenticatorSettings(configuration: Configuration): JWTAuthenticatorSettings =
    configuration.underlying.as[JWTAuthenticatorSettings]("silhouette.authenticator")

  /**
    * Provides the authenticator service.
    *
    * @param idGenerator The ID generator implementation.
    * @param configuration The Play configuration.
    * @param clock The clock instance.
    * @return The authenticator service.
    */
  @Provides
  def provideAuthenticatorService(idGenerator: IDGenerator,
                                  configuration: Configuration,
                                  authenticatorRepository: JWTAuthenticatorRepository,
                                  encoder: AuthenticatorEncoder,
                                  config: JWTAuthenticatorSettings,
                                  clock: Clock,
                                  executionContext: ExecutionContext): AuthenticatorService[JWTAuthenticator] = {
    new JWTAuthenticatorService(config, Some(authenticatorRepository), encoder, idGenerator, clock)(executionContext)
  }

  /**
    * Provides the avatar service.
    *
    * @param httpLayer The HTTP layer implementation.
    * @return The avatar service implementation.
    */
  @Provides
  def provideAvatarService(httpLayer: HTTPLayer): AvatarService = new GravatarService(httpLayer)

  /**
    * Provides the password hasher registry.
    *
    * @param passwordHasher The default password hasher implementation.
    * @return The password hasher registry.
    */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    new PasswordHasherRegistry(passwordHasher)
  }

  /**
    * Provides the credentials provider.
    *
    * @param authInfoRepository The auth info repository implementation.
    * @param passwordHasherRegistry The password hasher registry.
    * @return The credentials provider.
    */
  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry,
                                 executionContext: ExecutionContext): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)(executionContext)
  }

}
