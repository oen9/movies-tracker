val akkaVersion = "2.5.12"
val akkaHttpVersion = "10.1.1"

lazy val app = crossProject.in(file(".")).settings(
  unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
    "com.lihaoyi" %%% "upickle" % "0.4.4"
  ),
  scalaVersion := "2.12.6",
  name := "movies-tracker"
).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.2"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,

    "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.2",
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "com.github.scullxbones" %% "akka-persistence-mongo-casbah" % "2.0.7",
    "org.mongodb" %% "casbah" % "3.1.1",

    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.mindrot" % "jbcrypt" % "0.4",

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
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
