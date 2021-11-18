lazy val primaryName = "fs2-aws"
lazy val specs2Version = "4.13.0"
lazy val fs2Version = "2.5.10"

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
  crossScalaVersions := Seq("2.13.6", "2.12.15"),
  scalaVersion := crossScalaVersions.value.head,
  startYear := Option(2018),
  libraryDependencies ++= {
    Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "org.specs2" %%% "specs2-core" % specs2Version % Test,
      "org.specs2" %%% "specs2-cats" % "4.12.1" % Test,
    )
  },
  resolvers += Resolver.sonatypeRepo("releases"),

  githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11"),
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

lazy val fs2Utils = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(compilerOptions: _*)
  .settings(
    name := "fs2-utils",
    description := "Helpful utility functions for fs2 streams",
  )

lazy val fs2UtilsJVM = fs2Utils.jvm

lazy val fs2AwsUtils = (project in file("main"))
  .settings(compilerOptions: _*)
  .settings(
    name := primaryName,
    description := "Utility classes for interacting with the AWS SDKs from Scala using fs2",
    libraryDependencies ++= {
      val awsSdkVersion = "1.12.113"

      Seq(
        "co.fs2" %% "fs2-io" % fs2Version,
        "com.chuusai" %% "shapeless" % "2.3.7",
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.6.0",
        "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
        "com.amazonaws" % "aws-java-sdk-kms" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-cloudformation" % awsSdkVersion % Provided,
        "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion % Provided,
        "org.specs2" %% "specs2-mock" % specs2Version % Test,
      )
    },
  )
  .dependsOn(fs2UtilsJVM)

lazy val fs2Aws2Utils = (project in file("aws-java-sdk2"))
  .settings(compilerOptions: _*)
  .settings(
    name := primaryName + "-java-sdk2",
    description := "Utility classes for interacting with the V2 AWS Java SDKs from Scala using fs2",
    libraryDependencies ++= {
      Seq(
        "co.fs2" %% "fs2-reactive-streams" % fs2Version,
        "org.typelevel" %% "cats-tagless-macros" % "0.14.0",
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.1",
        "software.amazon.awssdk" % "kms" % "2.17.84" % Provided,
      )
    },
  )

lazy val lambdaIOApp = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("lambda-io-app"))
  .settings(compilerOptions: _*)
  .settings(
    name := primaryName + "-lambda-io-app",
    libraryDependencies ++= {
      val circeVersion = "0.14.1"
      Seq(
        "io.circe" %%% "circe-literal" % circeVersion,
        "io.circe" %%% "circe-generic-extras" % circeVersion,
        "io.circe" %%% "circe-parser" % circeVersion,
        "io.circe" %%% "circe-generic-extras" % circeVersion,
      )
    },
  )
  .jvmSettings(
    description := "IOApp for AWS Lambda Java runtime",
    libraryDependencies ++= {
      Seq(
        "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
        "com.amazonaws" % "aws-lambda-java-log4j2" % "1.2.0",
        "co.fs2" %% "fs2-io" % fs2Version,
        "org.typelevel" %% "log4cats-slf4j" % "1.3.1",
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.14.1",
        "org.apache.logging.log4j" % "log4j-api" % "2.14.1",
        "org.typelevel" %% "cats-tagless-macros" % "0.14.0",
        "org.tpolecat" %% "natchez-core" % "0.1.5",
        "org.specs2" %% "specs2-scalacheck" % specs2Version,
      )
    },
  )
  .jsSettings(
    description := "IOApp for AWS Lambda Node runtime",
    Compile / npmDependencies += ("@types/aws-lambda" -> "8.10.59"),
    scalacOptions += "-Wconf:src=src_managed/.*:s",
    stOutputPackage := "jsdep",
    stMinimize := Selection.AllExcept("@types/aws-lambda"),
  )
  .jsConfigure(_.enablePlugins(ScalablyTypedConverterGenSourcePlugin))

lazy val fs2TestKit: Project = (project in file("test-kit"))
  .settings(compilerOptions: _*)
  .settings(
    name := primaryName + "-testkit",
    description := "Test implementations of fs2-aws classes",
  )
  .dependsOn(fs2AwsUtils)

lazy val `fs2-aws` = (project in file("."))
  .settings(
    publish / skip := true,
  )
  .aggregate(fs2UtilsJVM, fs2Utils.js, fs2AwsUtils, fs2Aws2Utils, fs2TestKit, lambdaIOApp.jvm, lambdaIOApp.js)
