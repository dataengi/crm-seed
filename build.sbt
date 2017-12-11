import com.typesafe.sbt.SbtGit.git
import com.typesafe.sbt.packager.Keys.{dockerBaseImage, dockerExposedPorts, dockerUsername}

enablePlugins(DockerPlugin, JavaAppPackaging, GitVersioning)

lazy val root = (project in file("."))
  .settings(
    name := "CRM Seed",
    organization := "com.dataengi",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:reflectiveCalls",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
    ),
    resolvers ++= Seq(
      Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns),
      Resolver.jcenterRepo
    ),
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= ProjectDependencies.play
  )
  .enablePlugins(PlayScala)

sourceGenerators in Compile <+= AllTasks.versionCodeGenerator

git.useGitDescribe := true
git.baseVersion := "v0.0.0"
val tagRegexp    = "(v[0-9]+\\.[0-9]+\\.[0-9]+)".r
val commitRegexp = "(v[0-9]+\\.[0-9]+\\.[0-9]+)-([0-9]+)-(.*)".r
git.gitTagToVersionNumber := {
  case tagRegexp(v)                        => Some(v)
  case commitRegexp(v, commitNumber, hash) => Some(s"$v-r$commitNumber")
  case string: String                      => Some(string)
  case _                                   => None
}

packageName in Docker := "crm"

dockerBaseImage := "openjdk:8-jre"
dockerUsername := Some("dataengi")
dockerUpdateLatest := true
dockerExposedPorts := Seq(9000)
