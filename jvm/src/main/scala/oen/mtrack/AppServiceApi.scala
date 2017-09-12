package oen.mtrack

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout

import scala.concurrent.duration.DurationLong

class AppServiceApi(
  val system: ActorSystem
) extends AppService

trait AppService {

  implicit def system: ActorSystem

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))
  implicit val timeout = Timeout(5.seconds)

  val routes: Route = getStatic ~
    getStaticDev

  def getStatic: Route = get {
    pathSingleSlash {
      getFromResource("index.html")
    } ~
    path("movies-tracker-opt.js") {
      getFromResource("movies-tracker-opt.js")
    } ~
    pathPrefix("front-res") {
      getFromResourceDirectory("front-res")
    }
  }

  def getStaticDev: Route = get {
    path("dev") {
      getFromResource("index-dev.html")
    } ~
    path("movies-tracker-fastopt.js") {
      getFromResource("movies-tracker-fastopt.js")
    } ~
    path("movies-tracker-fastopt.js.map") {
      getFromResource("movies-tracker-fastopt.js.map")
    }
  }
}
