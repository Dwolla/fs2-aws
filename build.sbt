import sbtcrossproject.CrossType // needed until Scala.js 1.0 is released
import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val primaryName = "fs2-aws"
lazy val specs2Version = "4.3.5"
lazy val fs2Version = "1.0.2"

lazy val commonSettings = Seq(
  organization := "com.dwolla",
  homepage := Some(url("https://github.com/Dwolla/fs2-aws")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  releaseVersionBump := sbtrelease.Version.Bump.Minor,
  releaseCrossBuild := true,
  startYear := Option(2018),
  resolvers ++= Seq(
    Resolver.bintrayRepo("dwolla", "maven")
  ),
  libraryDependencies ++= {
    Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "org.specs2" %%% "specs2-core" % specs2Version % Test,
      "org.specs2" %%% "specs2-cats" % specs2Version % Test,
    )
  },
  dependencyOverrides ++= Seq(
    "org.typelevel" %%% "cats-core" % "1.5.0",
    "org.typelevel" %%% "cats-effect" % "1.1.0",
    "commons-logging" % "commons-logging" % "1.2",
  ),
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := homepage.value.map(_.toString),
  bintrayRepository := "maven",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

lazy val fs2Utils = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(Seq(
    name := "fs2-utils",
    bintrayPackage := "fs2-utils",
    description := "Helpful utility functions for fs2 streams",
  ) ++ commonSettings ++ bintraySettings: _*)

lazy val fs2UtilsJVM = fs2Utils.jvm

lazy val fs2AwsUtils = (project in file("main"))
  .settings(Seq(
    name := primaryName,
    bintrayPackage := primaryName,
    description := "Utility classes for interacting with the AWS SDKs from Scala using fs2",
    libraryDependencies ++= {
      val awsSdkVersion = "1.11.437"

      Seq(
        "co.fs2" %% "fs2-io" % fs2Version,
        "com.chuusai" %% "shapeless" % "2.3.3",
        "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
        "com.amazonaws" % "aws-java-sdk-kms" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-cloudformation" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion % Provided,
        "org.specs2" %% "specs2-mock" % specs2Version % Test,
        "com.dwolla" %% "scala-aws-utils-testkit" % "1.6.1" % Test
      )
    },
  ) ++ commonSettings ++ bintraySettings: _*)
  .dependsOn(fs2UtilsJVM)

lazy val fs2TestKit: Project = (project in file("test-kit"))
  .settings(Seq(
    name := primaryName + "-testkit",
    bintrayPackage := primaryName + "-testkit",
    description := "Test implementations of fs2-aws classes",
  ) ++ commonSettings ++ bintraySettings: _*)
  .dependsOn(fs2AwsUtils)

lazy val `fs2-aws` = (project in file("."))
  .settings(commonSettings ++ noPublishSettings: _*)
  .aggregate(fs2UtilsJVM, fs2Utils.js, fs2AwsUtils, fs2TestKit)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  Keys.`package` := file(""),
)
