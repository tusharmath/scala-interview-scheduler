name := "scala-interview-scheduler"

version := "0.1"

scalaVersion := "2.13.1"

lazy val zioVersion = "1.0.0-RC18-1"

libraryDependencies += "dev.zio" %% "zio"          % zioVersion
libraryDependencies += "dev.zio" %% "zio-test"     % zioVersion % "test"
libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioVersion % "test"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
