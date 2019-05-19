name := "Forum"

version := "0.2"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  // slick
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "com.typesafe.slick" %% "slick-hikaricp" % "latest.integration",
  // akka
  "com.typesafe.akka" % "akka-actor_2.12" % "latest.integration" ,
  "com.typesafe.akka" %% "akka-slf4j" % "latest.integration",
  "com.typesafe.akka" %% "akka-typed" % "latest.integration",
  "com.typesafe.akka" %% "akka-contrib" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-core" % "latest.integration",
  "com.typesafe.akka" %% "akka-http" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-spray-json" % "latest.integration",
    // testing
  "com.typesafe.akka" %% "akka-testkit" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration",
  // spray
  "io.spray" %% "spray-json" % "latest.integration",
  // database
  "com.h2database" % "h2" % "latest.integration",
  "org.postgresql" % "postgresql" % "latest.integration",
// jwt
  "com.pauldijou" %% "jwt-spray-json" % "2.1.0",
  // kebs
  "pl.iterators" %% "kebs-spray-json" % "1.6.2"


)

// flywayUrl := "jdbc:postgresql://localhost:5432/forum"

// flywayUser := "postgres"

// flywayPassword := "postgres"

scalacOptions ++= Seq("-deprecation", "-feature")