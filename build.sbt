import sbtcrossproject.CrossType // needed until Scala.js 1.0 is released
import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val primaryName = "fs2-aws"
lazy val specs2Version = "4.8.0"
lazy val fs2Version = "2.0.1"

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
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  dependencyOverrides ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.0.0",
    "org.typelevel" %%% "cats-effect" % "2.0.0",
    "commons-logging" % "commons-logging" % "1.2",
  ),
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := homepage.value.map(_.toString),
  bintrayRepository := "maven",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ => false }
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
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.2",
        "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
        "com.amazonaws" % "aws-java-sdk-kms" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-cloudformation" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion % Provided,
        "org.specs2" %% "specs2-mock" % specs2Version % Test,
      )
    },
  ) ++ commonSettings ++ bintraySettings: _*)
  .dependsOn(fs2UtilsJVM)

lazy val fs2Aws2Utils = (project in file("aws-java-sdk2"))
  .settings(Seq(
    name := primaryName + "-java-sdk2",
    bintrayPackage := primaryName + "-java-sdk2",
    description := "Utility classes for interacting with the V2 AWS Java SDKs from Scala using fs2",
    libraryDependencies ++= {

      Seq(
        "co.fs2" %% "fs2-reactive-streams" % fs2Version,
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.1",
        "software.amazon.awssdk" % "kms" % "2.7.18" % Provided,
      )
    },
  ) ++ commonSettings ++ bintraySettings: _*)

lazy val lambdaIOApp = (project in file("lambda-io-app"))
  .settings(Seq(
    name := primaryName + "-lambda-io-app",
    bintrayPackage := primaryName + "-lambda-io-app",
    description := "IOApp for AWS Lambda Java runtime",
    libraryDependencies ++= {
      val circeVersion = "0.12.2"
      Seq(
        "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
        "com.amazonaws" % "aws-lambda-java-log4j2" % "1.0.0",
        "co.fs2" %% "fs2-io" % fs2Version,
        "io.circe" %% "circe-literal" % circeVersion,
        "io.circe" %% "circe-generic-extras" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,
        "io.circe" %% "circe-generic-extras" % circeVersion,
        "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.0",
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.2",
        "org.apache.logging.log4j" % "log4j-api" % "2.11.2",
      )
    },
  ) ++ commonSettings ++ bintraySettings: _*)

lazy val fs2TestKit: Project = (project in file("test-kit"))
  .settings(Seq(
    name := primaryName + "-testkit",
    bintrayPackage := primaryName + "-testkit",
    description := "Test implementations of fs2-aws classes",
  ) ++ commonSettings ++ bintraySettings: _*)
  .dependsOn(fs2AwsUtils)

lazy val `fs2-aws` = (project in file("."))
  .settings(commonSettings ++ noPublishSettings: _*)
  .aggregate(fs2UtilsJVM, fs2Utils.js, fs2AwsUtils, fs2Aws2Utils, fs2TestKit, lambdaIOApp)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  Keys.`package` := file(""),
)
