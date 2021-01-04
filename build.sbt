name := "zio-hello-world"
organization := "ch.epfl.scala"
version := "1.0"

scalaVersion := "2.13.3"
val ZIOVersion = "1.0.0-RC21-2"


libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

libraryDependencies ++= Seq("dev.zio" %% "zio" % ZIOVersion) ++ spec

lazy val spec = Seq(
  "dev.zio" %% "zio-test" % ZIOVersion % Test,
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
