import sbtcrossproject.CrossType // needed until Scala.js 1.0 is released
import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val primaryName = "fs2-aws"

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
      "co.fs2" %%% "fs2-core" % "0.10.5",
    )
  },
  dependencyOverrides ++= Seq(
    "org.typelevel" %%% "cats-core" % "1.4.0",
    "org.typelevel" %%% "cats-effect" % "0.10.1",
  ),
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := homepage.value.map(_.toString),
  bintrayRepository := "maven",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

lazy val specs2Version = "4.3.0"

lazy val fs2Utils = crossProject(JSPlatform, JVMPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(Seq(
    name := "fs2-utils",
    bintrayPackage := "fs2-utils",
    description := "Helpful utility functions for fs2 streams",
    libraryDependencies ++= {
      Seq(
        "org.specs2" %% "specs2-core" % specs2Version % Test,
      )
    }
  ) ++ commonSettings ++ bintraySettings: _*)

lazy val fs2AwsUtils = (project in file("main"))
  .settings(Seq(
    name := primaryName,
    bintrayPackage := primaryName,
    description := "Utility classes for interacting with the AWS SDKs from Scala using fs2",
    libraryDependencies ++= {
      val awsSdkVersion = "1.11.437"

      Seq(
        "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
        "com.amazonaws" % "aws-java-sdk-kms" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-cloudformation" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion % Provided,
        "org.specs2" %% "specs2-core" % specs2Version % Test,
        "org.specs2" %% "specs2-mock" % specs2Version % Test,
        "com.dwolla" %% "scala-aws-utils-testkit" % "1.6.1" % Test
      )
    },
  ) ++ commonSettings ++ bintraySettings: _*)
  .dependsOn(fs2Utils.jvm)

lazy val fs2TestKit: Project = (project in file("test-kit"))
  .settings(Seq(
    name := primaryName + "-testkit",
    bintrayPackage := primaryName + "-testkit",
    description := "Test implementations of fs2-aws classes",
  ) ++ commonSettings ++ bintraySettings: _*)
  .dependsOn(fs2AwsUtils)

lazy val `fs2-aws` = (project in file("."))
  .settings(commonSettings ++ noPublishSettings: _*)
  .aggregate(fs2Utils.jvm, fs2Utils.js, fs2AwsUtils, fs2TestKit)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  Keys.`package` := file(""),
)
