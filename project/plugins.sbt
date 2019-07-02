addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.5")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.28")
addSbtPlugin("org.portable-scala" % "sbt-crossproject"         % "0.6.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")
addSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.2.0")

resolvers += Resolver.bintrayIvyRepo("dwolla", "sbt-plugins")
addSbtPlugin("com.dwolla.sbt" % "sbt-dwolla-base" % "1.3.1")
