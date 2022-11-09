ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"

libraryDependencies +=
  "org.typelevel" %% "cats-core" % "2.1.0"

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.14"
libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % "1.0.6" % Test
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
scalacOptions ++= Seq(
  "-Xfatal-warnings"
)
lazy val root = (project in file("."))
  .settings(
    name := "EssentialScala"
  )
