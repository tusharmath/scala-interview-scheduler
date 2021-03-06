name := "scala-interview-scheduler"

version := "0.1"

scalaVersion := "2.13.1"

// ZIO Core
lazy val zioVersion = "1.0.0-RC18"

// Project Scheduler
lazy val scheduler =
  (project in file("scheduler"))

// Project Storage
lazy val storage = (project in file("storage")).settings(
  libraryDependencies ++= Seq(
    "io.suzaku" %% "boopickle"   % "1.3.1",
    "dev.zio"   %% "zio-rocksdb" % "0.2.0"
  )
)

// Project Program
lazy val program = (project in file("program"))
  .dependsOn(scheduler)

// Testing
ThisBuild / libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % zioVersion,
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
)

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// Global Options
ThisBuild / scalacOptions ++= Seq(
  "-language:postfixOps",
  "-language:implicitConversions",
  "-feature"
)
