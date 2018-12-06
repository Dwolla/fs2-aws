addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.2")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")
addSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.1.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.25")

resolvers += Resolver.bintrayIvyRepo("dwolla", "sbt-plugins")
addSbtPlugin("com.dwolla.sbt" % "sbt-dwolla-base" % "1.2.0")
