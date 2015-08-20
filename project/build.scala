import sbt._
import Keys._
import org.scalatra.sbt._
import sbtassembly.AssemblyKeys._

object HakuperusteetBuild extends Build {
  val Organization = "fi.vm.sade"
  val Name = "Hakuperusteet"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.7"
  val ScalatraVersion = "2.3.1"
  val artifactory = "https://artifactory.oph.ware.fi/artifactory/"

  lazy val project = Project (
    "hakuperusteet",
    file("."),
    settings = ScalatraPlugin.scalatraWithJRebel ++ sbtassembly.AssemblyPlugin.assemblySettings ++
      addArtifact(Artifact("hakuperusteet", "assembly"), sbtassembly.AssemblyKeys.assembly) ++ Seq(
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
        "com.typesafe" % "config" % "1.3.0",
        "org.json4s" %% "json4s-native" % "3.2.11",
        "org.scalaz" %% "scalaz-core" % "7.1.3",
        "com.netaporter" %% "scala-uri" % "0.4.7",
        "commons-codec" % "commons-codec" % "1.6",
        "joda-time" % "joda-time" % "2.8.2"
      ),
      assemblyJarName in assembly := Name.toLowerCase + "-" + Version + "-assembly.jar",
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
      publishTo := {
        if (Version.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at artifactory + "/oph-sade-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
        else
          Some("releases" at artifactory + "/oph-sade-release-local")
      }
    )
  )
}
