name:= "Scala_API"
scalaVersion:= "2.12.4"
version:= "0.0.1"

libraryDependencies ++= {
  val circeVersion = "0.9.1"
  Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.0",
    "com.typesafe.akka" %% "akka-http-testkit"   % "10.1.0",
    "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",

    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,

    "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.1"
  )
}

