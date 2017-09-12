package oen.mtrack

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {
  val config = ConfigFactory.load(sys.env.getOrElse("STAGE", "application"))

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem("movies-tracker", config)
  implicit val materializer = ActorMaterializer()

  val api = new AppServiceApi(system)

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(api.routes, host, port = port)

  val log =  Logging(system.eventStream, "app-service")

  import scala.concurrent.ExecutionContext.Implicits.global
  bindingFuture.onComplete {
    case Success(serverBinding) =>
      log.info("Bound to {}", serverBinding.localAddress)
    case  Failure(t) =>
      log.error(t, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}
