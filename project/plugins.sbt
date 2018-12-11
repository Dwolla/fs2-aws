addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.10")
addSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.1.3")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")

resolvers += Resolver.bintrayIvyRepo("dwolla", "sbt-plugins")
addSbtPlugin("com.dwolla.sbt" % "sbt-dwolla-base" % "1.2.0")
