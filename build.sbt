import sbt.Keys.libraryDependencies

// Common project settings
ThisBuild / organization 	  := "com.lancearlaus"
ThisBuild / version 		    := "0.4.0-SNAPSHOT"
ThisBuild / scalaVersion	  := "2.12.8"
ThisBuild / scalacOptions   ++= Seq("-feature", "-deprecation")
ThisBuild / resolvers       += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"


// Common dependency versions
val akkaVersion                 = "2.5.25"
val akkaHttpVersion             = "10.1.9"
val springVersion               = "5.1.9.RELEASE"
val springBootVersion           = "2.1.7.RELEASE"

// Module ids for dependencies
lazy val logbackClassic             = "ch.qos.logback"              %  "logback-classic"              % "1.2.3"
lazy val scalaLogging               = "com.typesafe.scala-logging"  %% "scala-logging"                % "3.+"
lazy val scalatest                  = "org.scalatest"               %% "scalatest"                    % "3.0.8"
lazy val scalaArm                   = "com.jsuereth"                %% "scala-arm"                    % "2.0"
lazy val akkaActor                  = "com.typesafe.akka"           %% "akka-actor"                   % akkaVersion
lazy val akkaTestkit                = "com.typesafe.akka"           %% "akka-testkit"                 % akkaVersion
lazy val akkaStream                 = "com.typesafe.akka"           %% "akka-stream"                  % akkaVersion
lazy val akkaHttp                   = "com.typesafe.akka"           %% "akka-http"                    % akkaHttpVersion
lazy val akkaHttpTestkit            = "com.typesafe.akka"           %% "akka-http-testkit"            % akkaHttpVersion
lazy val akkaHttpSprayJson          = "com.typesafe.akka"           %% "akka-http-spray-json"         % akkaHttpVersion
lazy val springContext              = "org.springframework"         %  "spring-context"               % springVersion
lazy val springTest                 = "org.springframework"         %  "spring-test"                  % springVersion
lazy val springBootStarter          = "org.springframework.boot"    %  "spring-boot-starter"          % springBootVersion
lazy val springBootStarterTest      = "org.springframework.boot"    %  "spring-boot-starter-test"     % springBootVersion
lazy val springBootStarterWeb       = "org.springframework.boot"    %  "spring-boot-starter-web"      % springBootVersion
lazy val springBootStarterActuator  = "org.springframework.boot"    %  "spring-boot-starter-actuator" % springBootVersion

// Common publishing settings
ThisBuild / publishMavenStyle       := true
ThisBuild / publishArtifact in Test := false
ThisBuild / pomIncludeRepository    := { _ => false }
ThisBuild / publishTo               := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / pomExtra                :=
  <url>http://github.com/scalaspring/scalaspring</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:scalaspring/scalaspring.git</url>
    <connection>scm:git:git@github.com:scalaspring/scalaspring.git</connection>
  </scm>
  <developers>
    <developer>
      <id>lancearlaus</id>
      <name>Lance Arlaus</name>
      <url>http://lancearlaus.github.com</url>
    </developer>
  </developers>



// Project definitions
lazy val scalaspring = (project in file("."))
  .aggregate(
    `scalatest-spring`,
    `akka-spring-boot`,
    `akka-http-spring-boot`,
    `akka-http-spring-boot-actuator`
  )

lazy val `scalatest-spring` = project
  .settings(
    description         := "Integrates ScalaTest with Spring to manage test context lifecycle using standard Spring annotations and a stackable Scala trait",

    libraryDependencies ++= Seq(
      springContext,
      springTest,
      scalatest
    )
  )

lazy val `akka-spring-boot` = project
  .dependsOn(`scalatest-spring` % Test)
  .settings(
    description         := "Scala-based integration of Akka with Spring Boot.\nTwo-way Akka<->Spring configuration bindings and convention over configuration with sensible automatic defaults get your project running quickly.",

    // Compilation dependencies
    libraryDependencies ++= Seq(
      scalaLogging,
      akkaActor,
      springContext,
      springBootStarter
    ),
    // Runtime dependencies
    libraryDependencies += logbackClassic % Runtime,
    // Test dependencies
    libraryDependencies ++= Seq(
      springBootStarterTest,
      akkaTestkit
    ).map { _ % Test }
  )

lazy val `akka-http-spring-boot` = project
  .dependsOn(`akka-spring-boot`, `scalatest-spring` % Test)
  .settings(
    description         := "Integrates Scala Akka HTTP and Spring Boot for rapid, robust service development with minimal configuration.\nPre-configured server components require little more than a route to get a service up and running.",

    // Compilation dependencies
    libraryDependencies ++= Seq(
      akkaStream,
      akkaHttp,
      akkaHttpSprayJson
    ),
    // Runtime dependencies
    //libraryDependencies += logbackClassic % Runtime,
    // Test dependencies
    libraryDependencies ++= Seq(
      springBootStarterTest,
      akkaTestkit,
      akkaHttpTestkit,
      scalaArm
    ).map { _ % Test }
  )

lazy val `akka-http-spring-boot-actuator` = project
  .dependsOn(`akka-http-spring-boot`)
  .settings(
    description         := "Demonstrates the basics and beyond of how to use Akka HTTP with Spring Boot to create a typical REST-based microservice",

    // Compilation dependencies
    libraryDependencies ++= Seq(
      springBootStarterWeb,
      springBootStarterActuator
    ),
    // Runtime dependencies
    //libraryDependencies += logbackClassic % Runtime,
    // Test dependencies
    //libraryDependencies ++= Seq(
      //akkaHttpTestkit,
      //scalaArm
    //).map { _ % Test }
  )

