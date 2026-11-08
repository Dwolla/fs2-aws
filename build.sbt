lazy val fs2Version = "3.14.0"

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
ThisBuild / crossScalaVersions := Seq("3.9.0", "2.13.18", "2.12.21")
ThisBuild / startYear := Option(2018)

ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "mimaReportBinaryIssues", "doc")))
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"), JavaSpec.temurin("11"))
// Scala Native warns when it links with clang older than 16. The ubuntu-22.04 runner image tops out at
// clang 15, and every clang on the ubuntu-24.04 image is 16 or newer.
ThisBuild / githubWorkflowOSes := Seq("ubuntu-24.04")
ThisBuild / mergifyRequiredJobs ++= Seq("validate-steward")
ThisBuild / mergifyStewardConfig ~= { _.map {
  _.withAuthor("dwolla-oss-scala-steward[bot]")
    .withMergeMinors(true)
}}

// Scala Native adds its compiler plugin with -Xplugin, which Scala 3's scaladoc ignores but warns about.
// Scala 2's scaladoc typechecks with the compiler plugins (e.g. kind-projector), so they stay there.
lazy val dropCompilerPluginsFromScala3Doc: Setting[Task[Seq[String]]] =
  Compile / doc / scalacOptions := {
    val options = (Compile / doc / scalacOptions).value
    if (scalaBinaryVersion.value == "3") options.filterNot(_.startsWith("-Xplugin")) else options
  }

lazy val `fs2-utils` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(
    description := "Helpful utility functions for fs2 streams",
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "org.scalameta" %%% "munit" % "1.3.3" % Test,
      "com.eed3si9n.expecty" %%% "expecty" % "0.17.1" % Test,
      "org.typelevel" %%% "munit-cats-effect" % "2.2.1" % Test,
    ),
  )
  .nativeSettings(
    tlVersionIntroduced := Map("3" -> "3.0.0", "2.12" -> "3.0.0", "2.13" -> "3.0.0"),
    dropCompilerPluginsFromScala3Doc,
  )

lazy val `fs2-aws` = tlCrossRootProject
  .aggregate(`fs2-utils`.jvm, `fs2-utils`.js, `fs2-utils`.native)
