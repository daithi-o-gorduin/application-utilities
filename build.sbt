import com.typesafe.config.ConfigFactory
import scala.util.{Failure, Success, Try}

val libraryName = "application-utilities"

val bTVersion : String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val dependencies: Seq[ModuleID] = Seq(
  "com.typesafe.play"      % "play_2.11"               % "2.5.16",
  "com.cjww-dev.libs"      % "data-security_2.11"      % "2.11.0",
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "2.0.1",
  "ch.qos.logback"         % "logback-classic"         % "1.2.3"
)

lazy val library = Project(libraryName, file("."))
  .settings(
    version                              :=  bTVersion,
    scalaVersion                         :=  "2.11.12",
    organization                         :=  "com.cjww-dev.libs",
    resolvers                            ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "cjww-dev" at "http://dl.bintray.com/cjww-development/releases"
    ),
    libraryDependencies                  ++= dependencies,
    bintrayOrganization                  :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild :=  true,
    bintrayRepository                    :=  "releases",
    bintrayOmitLicense                   :=  true
  )
