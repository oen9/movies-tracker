package oen.mtrack

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import akka.pattern.ask
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout
import oen.mtrack.actors.Auth
import oen.mtrack.actors.Auth.{LoggedOut, Logout}
import oen.mtrack.akkahttpsupport.UpickleSupport._
import oen.mtrack.directives.AuthDirectives.auth

import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContextExecutor, Future}

class AppServiceApi(
  val system: ActorSystem,
  val authActor: ActorRef
) extends AppService

trait AppService {

  implicit def system: ActorSystem
  def authActor: ActorRef

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))
  implicit val timeout: Timeout = Timeout(5.seconds)
  implicit val ex: ExecutionContextExecutor = system.dispatcher

  val routes: Route = getStatic ~
    securedApi ~
    login ~
    logout ~
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

  def login: Route = post {
    pathPrefix("login") {
      entity(as[Data]) { data =>
        onSuccess(authActor ? data) {
          case t @ Token(Some(_)) => complete(t)
          case _ => reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("none", None)))
        }
      }
    }
  }

  def logout: Route = post {
    pathPrefix("logout") {
      parameter('token) { t =>
        onSuccess(authActor ? Logout(Token(Some(t)))) {
          case LoggedOut => complete("ok")
          case _ => reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("none", None)))
        }
      }
    }
  }

  def securedApi: Route = pathPrefix("secured") {
    auth(authenticator){ userRef =>
      complete(s"hello $userRef")
    }
  }

  def authenticator(token: String): Future[Option[ActorRef]] = {
    (authActor ? Token(Some(token))).map {
      case Auth.UserRef(Some(ref)) => Some(ref)
      case _ => None
    }
  }
}
