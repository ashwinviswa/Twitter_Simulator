name := "TwitterAPI"
 
version := "0.1"

 scalaVersion := "2.10.2"
 
 fork in run := true
 

libraryDependencies ++= {
  val akkaVersion = "2.2.0"
  Seq(
   "com.azavea.geotrellis" %% "geotrellis" % "0.9.0",
   "io.spray" % "spray-routing" % "1.2.0",
  "io.spray" % "spray-can" % "1.2.0",
  "io.spray" %%  "spray-json" % "1.2.5",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % "2.2-M3",
  "com.typesafe" % "config" % "1.2.0",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.0.12",
  "org.scalatest" %% "scalatest" % "2.0.M7" % "test"
 )}

libraryDependencies <++= scalaVersion(v =>
  Seq("org.scala-lang" % "scala-actors" % v)
)

