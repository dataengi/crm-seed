package com.dataengi.crm.common.context

import com.dataengi.crm.profiles.services.AvatarService
import com.dataengi.crm.common.docker.DockerPostgresService
import com.dataengi.crm.common.extensions.awaits._
import com.dataengi.crm.common.mocks.FakeAvatarService
import org.specs2.specification.core.{Fragments, SpecificationStructure}
import org.specs2.specification.create.FragmentsFactory
import org.specs2.specification.{BeforeAfterAll, Scope}
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Binding, Module}
import play.api.inject.bind

import scala.concurrent.ExecutionContext

trait CRMApplication extends Scope with WithInMemoryDataBase {

  val RunningMode: String

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  lazy val applicationBuilder = new GuiceApplicationBuilder()
    .overrides(bind[AvatarService].to[FakeAvatarService])
    .configure(overrideConf)

  lazy val application = applicationBuilder.build()

  def beforeTestingRunning() = {
    // override it for prepared tests
    println(Console.RED, "================================")
    println(Console.GREEN, s"Running $RunningMode")
    println(Console.GREEN, overrideConf.mkString("(", "\n", ")"))
    println(Console.RED, "================================")
  }

}

trait BaseConfiguration {

  val baseConf: Map[String, Any] = Map[String, Any](
    "play.db.ddl.print"       -> true,
    "play.evolutions.enabled" -> false,
    "play.db.create.dynamic"  -> true,
    "play.mailer.mock"        -> true
  )

  def overrideConf: Map[String, Any]

}

trait WithInMemoryDataBase extends BeforeAfterAll with BaseConfiguration { self: CRMApplication =>

  val RunningMode = "with in memory database"

  override def beforeAll() = {
    beforeTestingRunning()
  }

  override def afterAll() = {
    application.stop().await()
  }

  lazy val overrideConf: Map[String, Any] = baseConf ++ inMemoryDataBaseConf

  lazy val inMemoryDataBaseConf: Map[String, Any] = Map[String, Any](
    "slick.dbs.default.db.driver"   -> "org.h2.Driver",
    "slick.dbs.default.db.url"      -> "jdbc:h2:mem:crm_db;MODE=PostgreSQL;MVCC=true;LOCK_TIMEOUT=10000",
    "slick.dbs.default.db.user"     -> "sa",
    "slick.dbs.default.db.password" -> "root"
  )

}

trait WithTestDatabase extends BeforeAfterAll with BaseConfiguration { self: CRMApplication =>

  val RunningMode = "with test database"

  override def beforeAll() = {
    beforeTestingRunning()
  }

  override def afterAll() = {
    application.stop().await()
  }

  override lazy val overrideConf: Map[String, Any] = baseConf ++ testDataBaseConf

  lazy val TestDataBaseName = "test_crm"

  lazy val testDataBaseConf: Map[String, Any] = Map[String, Any](
    "slick.dbs.default.driver"            -> "slick.driver.PostgresDriver$",
    "slick.dbs.default.db.driver"         -> "org.postgresql.Driver",
    "slick.dbs.default.db.user"           -> "postgres",
    "slick.dbs.default.db.password"       -> "postgres",
    "slick.dbs.default.db.url"            -> s"jdbc:postgresql://localhost:5432/$TestDataBaseName",
    "slick.dbs.default.db.properties.url" -> s"jdbc:postgresql://localhost:5432/$TestDataBaseName"
  )

}

trait WithDockerService extends DockerPostgresService with BeforeAfterAllStopOnError with BaseConfiguration {
  self: CRMApplication =>

  val RunningMode = "with docker container"

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  override def afterAll() = {
    application.stop().await()
    stopAllQuietly()
  }

  override def beforeAll() = {
    startAllOrFail()
    beforeTestingRunning()
  }

  override lazy val overrideConf: Map[String, Any] = baseConf ++ dockerTestDataBaseConf

}

trait BeforeAfterAllStopOnError extends SpecificationStructure with FragmentsFactory {

  def beforeAll(): Unit
  def afterAll(): Unit

  override def map(fs: => Fragments) =
    super
      .map(fs)
      .prepend(
        fragmentFactory.step(beforeAll()).stopOnError
      )
      .append(fragmentFactory.step(afterAll()))
}
