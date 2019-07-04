name := "Forum"

version := "0.8"
scalaVersion := "2.12.7"


val akkaVersion = "2.5.11"
val akkaHttpVersion = "10.1.1"
val scalaTestVersion = "3.0.7"
val slickVersion = "3.2.2"
val sprayVersion = "1.3.4"
val kebsVersion = "1.6.2"
libraryDependencies ++= Seq(
  // slick
  "com.typesafe.slick" %% "slick" % slickVersion,
  // akka
  "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion ,
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
  "org.postgresql" % "postgresql" % "latest.integration",
  // kebs
  "pl.iterators" %% "kebs-akka-http" % kebsVersion,
  "pl.iterators" %% "kebs-spray-json" % kebsVersion,
  "pl.iterators" %% "kebs-slick" % kebsVersion
)

scalacOptions ++= Seq("-deprecation", "-feature")
