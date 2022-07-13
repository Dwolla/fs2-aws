lazy val fs2Version = "3.2.10"

inThisBuild(List(
  organization := "com.dwolla",
  homepage := Some(url("https://github.com/Dwolla/fs2-aws")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "bpholt",
      "Brian Holt",
      "bholt+github@dwolla.com",
      url("https://dwolla.com")
    )
  ),
  crossScalaVersions := Seq("2.13.8", "2.12.16"),
  scalaVersion := crossScalaVersions.value.head,
  startYear := Option(2018),
  resolvers += Resolver.sonatypeRepo("releases"),

  githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "mimaReportBinaryIssues", "doc"))),
  githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"), JavaSpec.temurin("11")),
  githubWorkflowTargetTags ++= Seq("v*"),
  githubWorkflowPublishTargetBranches :=
    Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
  githubWorkflowPublish := Seq(
    WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    )
  ),
  localMimaPreviousVersions := Set("3.0.0-RC1"),
))

lazy val compilerOptions = Seq(
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  Compile / scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => "-Ymacro-annotations" :: Nil
      case _ => Nil
    }
  },

  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => Nil
      case _ => compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :: Nil
    }
  },
)

lazy val `fs2-utils` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(compilerOptions: _*)
  .settings(
    description := "Helpful utility functions for fs2 streams",
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
      "com.eed3si9n.expecty" %%% "expecty" % "0.15.4" % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7" % Test,
    ),
  )

lazy val `fs2-aws-java-sdk2` = (project in file("aws-java-sdk2"))
  .settings(compilerOptions: _*)
  .settings(
    description := "Utility classes for interacting with the V2 AWS Java SDKs from Scala using fs2",
    libraryDependencies ++= {
      Seq(
        "co.fs2" %% "fs2-reactive-streams" % fs2Version,
        "org.typelevel" %% "cats-tagless-macros" % "0.14.0",
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.0",
        "software.amazon.awssdk" % "kms" % "2.17.230" % Provided,
      )
    },
  )

lazy val `fs2-aws` = (project in file("."))
  .settings(
    publish / skip := true,
    publishArtifact := false,
  )
  .aggregate(`fs2-utils`.jvm, `fs2-utils`.js, `fs2-aws-java-sdk2`)
