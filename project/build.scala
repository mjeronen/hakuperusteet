import sbt._
import Keys._
import org.scalatra.sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.{PathList, MergeStrategy}
import com.earldouglas.xwp.XwpPlugin._
import java.text.SimpleDateFormat
import java.util.Date

object HakuperusteetBuild extends Build {
  val Organization = "fi.vm.sade"
  val Name = "Hakuperusteet"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.7"
  val ScalatraVersion = "2.3.1"
  val http4sVersion = "0.10.0"
  val jettyVersion = "9.3.0.v20150612"
  val slickVersion = "3.1.0-RC1"

  val artifactory = "https://artifactory.oph.ware.fi/artifactory/"

  lazy val buildversion = taskKey[Unit]("start buildversion.txt generator")

  val buildversionTask = buildversion <<= version map {
    (ver: String) =>
      val now: String = new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date())
      val buildversionTxt: String = "artifactId=hakuperusteet\nversion=" + ver +
        "\nbuildNumber=" + sys.props.getOrElse("buildNumber", "N/A") +
        "\nbranchName=" + sys.props.getOrElse("branchName", "N/A") +
        "\nvcsRevision=" + sys.props.getOrElse("revisionNumber", "N/A") +
        "\nbuildTtime=" + now
      println("writing buildversion.txt:\n" + buildversionTxt)

      val f: File = file("src/main/resources/webapp/buildversion.txt")
      IO.write(f, buildversionTxt)
  }

  lazy val project = Project (
    "hakuperusteet",
    file("."),
    settings = ScalatraPlugin.scalatraWithJRebel ++ sbtassembly.AssemblyPlugin.assemblySettings ++
      addArtifact(Artifact("hakuperusteet", "assembly"), sbtassembly.AssemblyKeys.assembly) ++
      com.earldouglas.xwp.XwpPlugin.jetty() ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      resolvers += "OPH snapshots" at "https://artifactory.oph.ware.fi/artifactory/oph-sade-snapshot-local",
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "com.github.spullara.mustache.java" % "compiler" % "0.9.1",
        "org.http4s" %% "http4s-dsl"         % http4sVersion,
        "org.http4s" %% "http4s-argonaut"    % http4sVersion,
        "org.http4s" %% "http4s-blaze-client" % http4sVersion,
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
        "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
        "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container;compile",
        "org.eclipse.jetty" % "jetty-plus" % jettyVersion % "container",
        "org.eclipse.jetty" % "jetty-jmx" % jettyVersion % "container;compile",
        "javax.servlet" % "javax.servlet-api" % "3.1.0",
        "com.typesafe" % "config" % "1.3.0",
        "org.json4s" %% "json4s-native" % "3.2.11",
        "org.scalaz" %% "scalaz-core" % "7.1.3",
        "com.netaporter" %% "scala-uri" % "0.4.7",
        "commons-codec" % "commons-codec" % "1.6",
        "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
        "com.typesafe.slick" %% "slick" % slickVersion,
        "com.typesafe.slick" %% "slick-codegen" % slickVersion,
        "org.flywaydb" % "flyway-core" % "3.2.1",
        "com.google.api-client" % "google-api-client" % "1.20.0",
        "org.apache.httpcomponents" % "fluent-hc" % "4.5",
        "fi.vm.sade" %% "scala-utils" % "0.2.0-SNAPSHOT"
      ),
      libraryDependencies ++= Seq(
        "org.scalatest" % "scalatest_2.11" % "2.2.4",
        "org.typelevel" %% "scalaz-scalatest" % "0.2.2"
      ).map(_ % "test"),
      mainClass in (Compile, run) := Some("fi.vm.sade.hakuperusteet.HakuperusteetServer"),
      compile <<= (compile in Compile) dependsOn npmInstallTask,
      compile <<= (compile in Compile) dependsOn npmBuildTask,
      npmInstallTask := { "npm install" !},
      npmBuildTask := { "npm run build" !},

      mainClass in assembly := Some("fi.vm.sade.hakuperusteet.HakuperusteetServer"),
      test in assembly := {},
      assemblyJarName in assembly := Name.toLowerCase + "-" + Version + "-assembly.jar",
      assemblyMergeStrategy in assembly := {
        case PathList("logback.xml") => MergeStrategy.discard
        case PathList("reference.conf") => MergeStrategy.discard
        case PathList("mockReference.conf") => MergeStrategy.discard
        case x => (assemblyMergeStrategy in assembly).value(x)
      },
      assemblyExcludedJars in assembly := {
        val cp = (fullClasspath in assembly).value
        cp filter {_.data.getName == "guava-jdk5-13.0.jar"}
      },
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
      buildversionTask,
      publishTo := {
        if (Version.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at artifactory + "/oph-sade-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
        else
          Some("releases" at artifactory + "/oph-sade-release-local")
      }
    )
  )

  lazy val npmInstallTask = taskKey[Unit]("Execute the npm install command")
  lazy val npmBuildTask = taskKey[Unit]("Execute the npm build command")
}
