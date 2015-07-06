import sbt._
import Keys._
import org.scalatra.sbt._

object HakuperusteetBuild extends Build {
  val Organization = "fi.vm.sade"
  val Name = "Hakuperusteet"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.7"
  val ScalatraVersion = "2.3.1"

  lazy val project = Project (
    "hakuperusteet",
    file("."),
    settings = ScalatraPlugin.scalatraWithJRebel ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.3.0.v20150612" % "container;compile",
        "org.eclipse.jetty" % "jetty-plus" % "9.3.0.v20150612" % "container",
        "javax.servlet" % "javax.servlet-api" % "3.1.0",
        "org.json4s" %% "json4s-native" % "3.2.11"
      )
    )
  )
}
