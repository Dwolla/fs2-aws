lazy val fs2Version = "3.11.0"

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
ThisBuild / tlCiReleaseBranches := Seq("3.x")
ThisBuild / sonatypeCredentialHost := xerial.sbt.Sonatype.sonatypeLegacy
ThisBuild / crossScalaVersions := Seq("3.3.3", "2.13.14", "2.12.20")
ThisBuild / startYear := Option(2018)

ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "mimaReportBinaryIssues", "doc")))
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"), JavaSpec.temurin("11"))
ThisBuild / mergifyRequiredJobs ++= Seq("validate-steward")
ThisBuild / mergifyStewardConfig ~= { _.map {
  _.withAuthor("dwolla-oss-scala-steward[bot]")
    .withMergeMinors(true)
}}

lazy val `fs2-utils` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(
    description := "Helpful utility functions for fs2 streams",
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "org.scalameta" %%% "munit" % "1.0.2" % Test,
      "org.typelevel" %%% "munit-cats-effect" % "2.0.0" % Test,
    ),
  )

lazy val `fs2-aws` = tlCrossRootProject
  .aggregate(`fs2-utils`.jvm, `fs2-utils`.js)
