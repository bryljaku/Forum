name := "Forum"

version := "0.8"
scalaVersion := "2.12.7"


val akkaVersion = "2.5.23"
val akkaHttpVersion = "10.1.8"
val scalaTestVersion = "3.0.7"
val slickVersion = "3.2.3"
val sprayVersion = "1.3.5"
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  // slick
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  // akka
  "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion ,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    // testing
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  // spray
  "io.spray" %% "spray-json" % sprayVersion,
  // database
  "com.h2database" % "h2" % "latest.integration",
  "org.postgresql" % "postgresql" % "latest.integration",
  // kebs
  "pl.iterators" %% "kebs-akka-http" % "1.6.2",
  "pl.iterators" %% "kebs-spray-json" % "1.6.2",
  "pl.iterators" %% "kebs-slick" % "1.6.2"
)

scalacOptions ++= Seq("-deprecation", "-feature")
