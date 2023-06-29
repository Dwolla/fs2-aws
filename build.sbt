lazy val fs2Version = "3.7.0"

ThisBuild / organization := "com.dwolla"
ThisBuild / homepage := Some(url("https://github.com/Dwolla/fs2-aws"))
ThisBuild / licenses += ("MIT", url("https://opensource.org/licenses/MIT"))
ThisBuild / developers := List(
  Developer(
    "bpholt",
    "Brian Holt",
    "bholt+github@dwolla.com",
    url("https://dwolla.com")
  )
)
ThisBuild / tlBaseVersion := "3.0"
ThisBuild / crossScalaVersions := Seq("2.13.10", "2.12.18")
ThisBuild / scalaVersion := crossScalaVersions.value.head
ThisBuild / startYear := Option(2018)
ThisBuild / tlMimaPreviousVersions ++= Set("3.0.0-RC1")

ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "mimaReportBinaryIssues", "doc")))
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"), JavaSpec.temurin("11"))
ThisBuild / mergifyRequiredJobs ++= Seq("validate-steward")
ThisBuild / mergifyStewardConfig ~= { _.map(_.copy(
  author = "dwolla-oss-scala-steward[bot]",
  mergeMinors = true,
))}

lazy val `fs2-utils` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(
    description := "Helpful utility functions for fs2 streams",
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
      "com.eed3si9n.expecty" %%% "expecty" % "0.16.0" % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7" % Test,
    ),
  )

lazy val `fs2-aws-java-sdk2` = project
  .in(file("aws-java-sdk2"))
  .settings(
    description := "Utility classes for interacting with the V2 AWS Java SDKs from Scala using fs2",
    libraryDependencies ++= {
      Seq(
        "co.fs2" %% "fs2-reactive-streams" % fs2Version,
        "org.typelevel" %% "cats-tagless-macros" % "0.15.0",
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.11.0",
        "software.amazon.awssdk" % "kms" % "2.20.95" % Provided,
      )
    },
  )

lazy val `fs2-aws` = project
  .in(file("."))
  .aggregate(`fs2-utils`.jvm, `fs2-utils`.js, `fs2-aws-java-sdk2`)
  .enablePlugins(NoPublishPlugin)
