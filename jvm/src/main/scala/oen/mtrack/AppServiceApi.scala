package oen.mtrack

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import akka.pattern.ask
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout
import oen.mtrack.actors.{Auth, User}
import oen.mtrack.actors.Auth.{LoggedOut, Logout, RegisterSucced}
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
    register ~
    moviesOperations ~
    getStaticDev ~
    workbenchFix

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

  def workbenchFix: Route = path("notifications") {
    Route { context =>
      val headersWithoutTimeoutAccess = context.request.headers.filterNot(_.name() == "Timeout-Access")
      val request = context.request.copy(headers = headersWithoutTimeoutAccess)

      val flow = Http(system).outgoingConnection(request.uri.authority.host.address(), 12345)
      Source.single(request)
        .via(flow)
        .runWith(Sink.head)
        .flatMap(context.complete(_))
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

  def register: Route = post {
    pathPrefix("register") {
      entity(as[Data]) { data =>
        onSuccess(authActor ? data) {
          case RegisterSucced => complete(StatusCodes.Created)
          case _ => complete(StatusCodes.Conflict)
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

  def moviesOperations: Route = pathPrefix("movies") {
    auth(authenticator) { user =>
      def askUser(cmd: User.cmd): Route = {
        onSuccess(user ? cmd) {
          case m: Data => complete(m)
          case User.Success => complete(StatusCodes.NoContent)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }

      get {
        pathPrefix("get-movies") { askUser(User.GetMovies) }
      } ~
      post {
        pathPrefix("add-or-update" / IntNumber) { id => askUser(User.AddOrUpdateMovie(id)) } ~
        pathPrefix("remove" / IntNumber) { id => askUser(User.RemoveMovie(id)) } ~
        pathPrefix("update-season"/ IntNumber) { id =>
          entity(as[Data]) {
            case s: Season => askUser(User.UpdateCurrentSeason(id, s))
            case _ => complete(StatusCodes.BadRequest)
          }
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
