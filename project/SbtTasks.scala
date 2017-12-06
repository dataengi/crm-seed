import sbt.Keys.{baseDirectory, sourceManaged, target, version}
import sbt.{File, Process, Project, TaskKey}

object AllTasks extends GenerateGitVersionTask

trait GenerateGitVersionTask {
  import sbt._

  def versionCodeGenerator = {
    (sourceManaged in Compile, version) map { (d, v) =>
      val file = d / "BuildInfo.scala"
      IO.write(file,
        """package controllers
          |object BuildInfo {
          |  val version = "%s"
          |}
          | """.stripMargin.format(v))
      Seq(file)
    }
  }
}
