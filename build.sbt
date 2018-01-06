val akkaVersion = "2.5.8"
val akkaHttpVersion = "10.0.11"

lazy val app = crossProject.in(file(".")).settings(
  unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
    "com.lihaoyi" %%% "upickle" % "0.4.4"
  ),
  scalaVersion := "2.12.4",
  name := "movies-tracker"
).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.3",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.2"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.mindrot" % "jbcrypt" % "0.4",

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
)

lazy val fastOptJSDev = TaskKey[Unit]("fastOptJSDev")
lazy val appJS = app.js
  .disablePlugins(RevolverPlugin)
  .enablePlugins(WorkbenchPlugin)
  .settings(
    fastOptJSDev := {

      // resources
      val targetRes = "../target/scala-2.12/classes/"
      IO.copyDirectory((resourceDirectory in Compile).value, new File(baseDirectory.value, targetRes))

      // fastopt.js
      val fastOptFrom = (fastOptJS in Compile).value.data
      val fastOptTo = new File(baseDirectory.value, targetRes + fastOptFrom.name)
      IO.copyFile(fastOptFrom, fastOptTo)

      // fastopt.js.map
      val mapFileName = fastOptFrom.name + ".map"
      val fastOptMapFrom = fastOptFrom.getParentFile / mapFileName
      val fastOptMapTo = new File(baseDirectory.value, targetRes + mapFileName)
      IO.copyFile(fastOptMapFrom, fastOptMapTo)
    },
    scalacOptions += "-P:scalajs:sjsDefinedByDefault"
  )

lazy val appJVM = app.jvm.settings(
  (resources in Compile) += (fullOptJS in (appJS, Compile)).value.data,
  (unmanagedResourceDirectories in Compile) += (resourceDirectory in (appJS, Compile)).value,
  target := baseDirectory.value / ".." / "target"
).enablePlugins(JavaAppPackaging)

disablePlugins(RevolverPlugin)
