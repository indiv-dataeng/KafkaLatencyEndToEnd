import scala.Console.in

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.2"

lazy val root = (project in file("."))
  .settings(
    name := "IndivEndToEnd"
  )
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.6.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.2"

//  "org.typelevel" %% "cats-core" % "2.1.1",
//  "ch.qos.logback" % "logback-classic" % "1.2.3",
//  "com.github.pureconfig" %% "pureconfig" % "0.13.0",
//  "com.sksamuel.avro4s" %% "avro4s-core" % "3.1.1",
//  "com.nrinaudo" %% "kantan.csv" % "0.6.1",
//  "com.nrinaudo" %% "kantan.csv-enumeratum" % "0.6.1",
//  "io.confluent" % "kafka-avro-serializer" % "6.0.0",
//  "org.scalatest" %% "scalatest" % "3.2.3",
//  "org.scalactic" %% "scalactic" % "3.2.3",
//  "org.scalacheck" %% "scalacheck" % "1.15.1",
//  "org.typelevel" %% "cats-core" % "2.3.0",
//)
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

