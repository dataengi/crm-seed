resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(
  Resolver.ivyStylePatterns)

// The Play plugin
//val playVersion: String = "2.5.13"
val playVersion: String = "2.6.7"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % playVersion)

//addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.4.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")

// monocle plugin

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)