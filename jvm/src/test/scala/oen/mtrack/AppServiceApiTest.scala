package oen.mtrack

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestKit
import oen.mtrack.actors.{Auth, User}
import oen.mtrack.akkahttpsupport.UpickleSupport._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class AppServiceApiTest extends WordSpecLike with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  val tokenValue = "testToken"
  val tokenTest = Token(Some(tokenValue))
  val credential = Credential("testname", "testpasswd")
  val wrongCredential = Credential("wrong1", "wrong2")
  val movie = Movie(1, "foo", IndexedSeq(Season(2, 2)), Some("posterSrc"), Some("backdropSrc"), Season())
  val movies = Movies(IndexedSeq(movie))

  class AuthTestActor(user: ActorRef) extends Actor {
    override def receive: Receive = {
      case Auth.Login(`credential`) => sender() ! tokenTest
      case _: Auth.Login => sender() ! Token(None)
      case Auth.TryRegister(`credential`) => sender() ! Auth.RegisterSucced
      case _: Auth.TryRegister => sender() ! Auth.RegisterFailed
      case Auth.Logout(`tokenTest`) => sender() ! Auth.LoggedOut
      case _: Auth.Logout => sender() ! Auth.LogoutFailed
      case Auth.GetUserRefByToken(_) => sender() ! Auth.UserRef(Some(user))
    }
  }

  class UserTestActor extends Actor {
    override def receive: Receive = {
      case User.GetMovies => sender() ! movies
      case User.Search(_) => sender() ! movie
      case User.AddOrUpdateMovie(_) => sender() ! movie
      case User.RemoveMovie(_) => sender() ! User.Success
      case User.UpdateCurrentSeason(_, _) => sender() ! User.Success
      case _ => sender() ! "none"
    }
  }

  val user = system.actorOf(Props(new UserTestActor))
  val auth = system.actorOf(Props(new AuthTestActor(user)))
  val service = new AppServiceApi(system, auth)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The auth api service" should {
    "login" in {
      Post("/login", credential) ~> service.login ~> check {
        responseAs[Data] shouldEqual tokenTest
      }
    }

    "reject login" in {
      Post("/login", wrongCredential) ~> service.login ~> check {
        rejection
      }
    }

    "register" in {
      Post("/register", Register(credential)) ~> service.register ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    "reject registration" in {
      Post("/register", Register(wrongCredential)) ~> service.register ~> check {
        status shouldEqual StatusCodes.Conflict
      }
    }

    "logout" in {
      Post(s"/logout?token=$tokenValue") ~> service.logout ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "reject logout" in {
      Post("/logout?token=wrong") ~> service.logout ~> check {
        rejection
      }
    }
  }

  "The user api service" should {
    "get movies" in {
      Get("/movies/get-movies?token=123") ~> service.moviesOperations ~> check {
        responseAs[Data] shouldEqual movies
      }
    }

    "search movies" in {
      Get("/movies/search?token=123&query=foo") ~> service.moviesOperations ~> check {
        responseAs[Data] shouldEqual movie
      }
    }

    "add or update movie" in {
      Post("/movies/add-or-update/456?token=123") ~> service.moviesOperations ~> check {
        responseAs[Data] shouldEqual movie
      }
    }

    "remove movie" in {
      Post("/movies/remove/456?token=123") ~> service.moviesOperations ~> check {
        status shouldEqual StatusCodes.NoContent
      }
    }

    "update current season" in {
      Post("/movies/update-season/456?token=123", Season(2, 2)) ~> service.moviesOperations ~> check {
        status shouldEqual StatusCodes.NoContent
      }
    }
  }
}
