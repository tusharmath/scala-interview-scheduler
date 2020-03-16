name := "scala-interview-scheduler"

version := "0.1"

scalaVersion := "2.13.1"

lazy val zioVersion = "1.0.0-RC18-1"

libraryDependencies += "dev.zio" %% "zio"          % zioVersion
libraryDependencies += "dev.zio" %% "zio-test"     % zioVersion % "test"
libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioVersion % "test"

libraryDependencies += "dev.zio" %% "zio-rocksdb" % "0.2.0"

libraryDependencies += "io.suzaku" %% "boopickle" % "1.3.1"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

scalacOptions ++= Seq(
  "-language:postfixOps"
)
