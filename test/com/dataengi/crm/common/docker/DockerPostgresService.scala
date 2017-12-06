package com.dataengi.crm.common.docker

import java.sql.DriverManager

import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import com.whisk.docker.impl.spotify.SpotifyDockerFactory
import com.whisk.docker.{
  DockerCommandExecutor,
  DockerContainer,
  DockerContainerState,
  DockerFactory,
  DockerKit,
  DockerReadyChecker
}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait DockerPostgresService extends DockerKit {

  import scala.concurrent.duration._

  private lazy val log = LoggerFactory.getLogger(this.getClass)

  private val client: DockerClient = DefaultDockerClient.fromEnv().build()

  override implicit val dockerFactory: DockerFactory = new SpotifyDockerFactory(client)

  def PostgresAdvertisedPort = 5432
  def PostgresExposedPort    = 44444
  val PostgresUser           = "nph"
  val PostgresPassword       = "suitup"

  lazy val DockerPostgresHost: String = postgresContainer.hostname.getOrElse("localhost")
  lazy val DockerPostgresPort: Int    = PostgresExposedPort
  lazy val DockerDatabaseName: String = "crm"

  val postgresContainer: DockerContainer = DockerContainer("postgres:9.5")
    .withPorts((PostgresAdvertisedPort, Some(PostgresExposedPort)))
    .withEnv(s"POSTGRES_USER=$PostgresUser", s"POSTGRES_PASSWORD=$PostgresPassword")
    .withCommand()
    .withReadyChecker(
      PostgresReadyChecker(DockerDatabaseName, PostgresUser, PostgresPassword, Some(PostgresExposedPort))
        .looped(15, 1.second)
    )

  lazy val dockerTestDataBaseConf: Map[String, Any] = Map[String, Any](
    "slick.dbs.default.driver"            -> "slick.driver.PostgresDriver$",
    "slick.dbs.default.db.driver"         -> "org.postgresql.Driver",
    "slick.dbs.default.db.user"           -> PostgresUser,
    "slick.dbs.default.db.password"       -> PostgresPassword,
    "slick.dbs.default.db.url"            -> s"jdbc:postgresql://$DockerPostgresHost:$DockerPostgresPort/crm",
    "slick.dbs.default.db.properties.url" -> s"jdbc:postgresql://$DockerPostgresHost:$DockerPostgresPort/crm"
  )

  override def dockerContainers: List[DockerContainer] = postgresContainer :: super.dockerContainers
}

case class PostgresReadyChecker(databaseName: String, user: String, password: String, port: Option[Int] = None)
    extends DockerReadyChecker {

  override def apply(container: DockerContainerState)(implicit docker: DockerCommandExecutor,
                                                      ec: ExecutionContext): Future[Boolean] =
    container
      .getPorts()
      .map(ports =>
        Try {
          Class.forName("org.postgresql.Driver")
          val url = s"jdbc:postgresql://${docker.host}:${port.getOrElse(ports.values.head)}/"
          println(s"[postgres][docker][url] $url")
          Option(DriverManager.getConnection(url, user, password))
            .map { connection =>
              println(s"[posgres][docker][create-db][connection] isClosed=${connection.isClosed}")
              val statements = connection.createStatement()
              val result     = statements.executeUpdate(s"CREATE DATABASE $databaseName")
              println(s"[posgres][docker][create-db] result=$result")
              connection
            }
            .map(_.close)
            .isDefined
        }.getOrElse(false))
}
