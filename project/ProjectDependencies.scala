import sbt._
import play.sbt.PlayImport._
import sbt.Keys._

object Version {
  val playSlick           = "3.0.1"
  val playSlickEvolutions = "3.0.1"
  val h2                  = "1.4.193"
  val postgresql          = "9.4.1212"
  val shapeless           = "2.3.2"
  val monocle             = "1.4.0"
  val silhouette          = "5.0.0"
  val specs2              = "3.8.8"
  val dockerTestKit       = "0.9.0"
  val slickless           = "0.3.0"
  val ficus               = "1.2.6"
  val scalaCacheGuava     = "0.9.4"
  val playMailer          = "6.0.1"
  val nscalaTime          = "2.16.0"
  val scalaGuice          = "4.1.0"
  val spoiwo              = "1.1.1"
  val scalty              = "0.4.1"
  val pegdown             = "1.6.0"
  val scalatestPlusPlay   = "1.5.1"
  val playJson            = "2.6.0"
  val playSwagger         = "1.6.0"
  val swaggerUi           = "3.0.19"
}

object ProjectDependencies {

  private val silhouette = Seq(
    "com.mohiva" %% "play-silhouette"                 % Version.silhouette,
    "com.mohiva" %% "play-silhouette-persistence"     % Version.silhouette,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % Version.silhouette,
    "com.mohiva" %% "play-silhouette-crypto-jca"      % Version.silhouette
  )

  private val db = Seq(
    "com.typesafe.play" %% "play-slick"            % Version.playSlick,
    "com.typesafe.play" %% "play-slick-evolutions" % Version.playSlickEvolutions,
    "com.h2database"    % "h2"                     % Version.h2,
    "org.postgresql"    % "postgresql"             % Version.postgresql
  )

  private val app = Seq(
    filters,
    guice,
    cacheApi,
    ehcache,
    "com.chuusai"                    %% "shapeless"         % Version.shapeless,
    "io.underscore"                  % "slickless_2.11"     % Version.slickless,
    "com.iheart"                     %% "ficus"             % Version.ficus,
    "com.github.cb372"               %% "scalacache-guava"  % Version.scalaCacheGuava,
    "com.typesafe.play"              %% "play-json"         % Version.playJson,
    "com.typesafe.play"              %% "play-mailer"       % Version.playMailer,
    "com.typesafe.play"              %% "play-mailer-guice" % Version.playMailer,
    "com.github.nscala-time"         %% "nscala-time"       % Version.nscalaTime,
    "net.codingwell"                 %% "scala-guice"       % Version.scalaGuice,
    "com.norbitltd"                  % "spoiwo_2.11"        % Version.spoiwo,
    "com.github.julien-truffaut"     %% "monocle-core"      % Version.monocle,
    "com.github.julien-truffaut"     %% "monocle-macro"     % Version.monocle,
    "com.github.awesome-it-ternopil" % "scalty_2.11"        % Version.scalty
  )

  private val test = Seq(
    specs2                       % Test excludeAll ExclusionRule("org.specs2"),
    "com.github.julien-truffaut" %% "monocle-law"                 % Version.monocle           % Test,
    "org.pegdown"                % "pegdown"                      % Version.pegdown           % Test,
    "org.specs2"                 %% "specs2-core"                 % Version.specs2            % Test,
    "org.specs2"                 %% "specs2-junit"                % Version.specs2            % Test,
    "org.specs2"                 %% "specs2-html"                 % Version.specs2            % Test,
    "org.scalatestplus.play"     %% "scalatestplus-play"          % Version.scalatestPlusPlay % Test,
    "com.whisk"                  %% "docker-testkit-scalatest"    % Version.dockerTestKit     % Test,
    "com.whisk"                  %% "docker-testkit-impl-spotify" % Version.dockerTestKit     % Test,
    "com.mohiva"                 %% "play-silhouette-testkit"     % Version.silhouette        % Test
  )

  private val docs = Seq(
    "io.swagger"  %% "swagger-play2" % Version.playSwagger,
    "org.webjars" % "swagger-ui"     % Version.swaggerUi
  )

  val play: Seq[ModuleID] = app ++ silhouette ++ db ++ test ++ docs

}
