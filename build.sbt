name := "scala-interview-scheduler"

version := "0.1"

scalaVersion := "2.13.1"

// ZIO Core
lazy val zioVersion = "1.0.0-RC18-1"

// Project Prelude
lazy val prelude = project in file("prelude")

// Project Scheduler
lazy val scheduler =
  (project in file("scheduler"))
    .dependsOn(combinationBuilder, prelude)

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

// Project CombinationBuilder
lazy val combinationBuilder =
  (project in file("combinationBuilder")).dependsOn(prelude)

// Testing
ThisBuild / libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % zioVersion,
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
)

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// Global Options
ThisBuild / scalacOptions ++= Seq(
  "-language:postfixOps"
)
