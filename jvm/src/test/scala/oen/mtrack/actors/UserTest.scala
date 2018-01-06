package oen.mtrack.actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import oen.mtrack._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class UserTest() extends TestKit(ActorSystem("UserTest")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val testName = "test321"
  val exampleMovie1 = Movie(19, "Mr. Robot", Vector(Season(1, 10), Season(2, 10)), Some("/123.jpg"), Some("/456.jpg"), Season(1, 9))
  val exampleMovie2 = Movie(322, "Dexter", Vector(Season(1, 12), Season(2, 12), Season(3, 12)), Some("/abc.jpg"), Some("/def.jpg"), Season(3, 12))
  val exampleMovie3 = Movie(123, "Himym", Vector(Season(1, 24), Season(2, 24), Season(3, 24), Season(4, 24)), Some("/ha.jpg"), Some("/hb.jpg"))
  val dataToUpdateMovie3 = Movie(123, "Himym", Vector(Season(1, 24), Season(2, 24), Season(3, 24), Season(4, 24), Season(5,24)), Some("/ha.jpg"), Some("/hb.jpg"))
  val exampleMovies = Map(exampleMovie1.id -> exampleMovie1, exampleMovie2.id -> exampleMovie2)

  val exampleSearchMovie1 = SearchMovie(632, "Dragon", Some("/ssss.jpg"))

  "An User actor" should {

    "return name" in {
      val user = system.actorOf(User.props(testName))
      user ! User.GetName
      expectMsg(testName)
    }

    "return movies list" in {
      val user = system.actorOf(User.props(testName, exampleMovies))
      user ! User.GetMovies
      expectMsg(Movies(IndexedSeq(exampleMovie1, exampleMovie2)))
    }

    "remove movie" in {
      val user = system.actorOf(User.props(testName, exampleMovies))
      user ! User.RemoveMovie(exampleMovie1.id)
      expectMsg(User.Success)
      user ! User.GetMovies
      expectMsg(Movies(IndexedSeq(exampleMovie2)))
    }

    "update current season" in {
      val user = system.actorOf(User.props(testName, exampleMovies))

      val newSeason = Season(9, 99)
      user ! User.UpdateCurrentSeason(exampleMovie2.id, newSeason)
      expectMsg(User.Success)

      user ! User.GetMovies
      expectMsgClass(classOf[Movies])
        .movies
        .find(_.id == exampleMovie2.id)
        .fold(fail("movie not found"))(m => assert(m.currentSeason == newSeason))
    }

    "add new movie" in {
      val props = Props(new User(testName, Map()) {
        override def fetchMovie(id: Int, toRespond: ActorRef): Unit = self ! User.ToAdd(toRespond, exampleMovie3)
      })

      val user = system.actorOf(props)
      user ! User.AddOrUpdateMovie(exampleMovie3.id)
      expectMsg(exampleMovie3)

      user ! User.GetMovies
      expectMsg(Movies(IndexedSeq(exampleMovie3)))
    }

    "update movie without changing current season" in {
      val testId = exampleMovie3.id
      val testSeason = Season(2, 22)
      val updatedMovie = dataToUpdateMovie3.copy(id = testId, currentSeason = testSeason)

      val props = Props(new User(testName, Map(exampleMovie3.id -> exampleMovie3.copy(currentSeason = testSeason))) {
        override def fetchMovie(id: Int, toRespond: ActorRef): Unit = self ! User.ToAdd(toRespond, dataToUpdateMovie3.copy(id = id))
      })

      val user = system.actorOf(props)
      user ! User.AddOrUpdateMovie(testId)
      expectMsg(updatedMovie)

      user ! User.GetMovies
      expectMsg(Movies(IndexedSeq(updatedMovie)))
    }

    "search movies by query" in {
      val props = Props(new User(testName, exampleMovies) {
        override def search(query: String, toRespond: ActorRef): Unit = toRespond ! SearchMovies(IndexedSeq(exampleSearchMovie1))
      })

      val user = system.actorOf(props)
      user ! User.Search("example query")
      expectMsg(SearchMovies(IndexedSeq(exampleSearchMovie1)))

      user ! User.GetMovies
      expectMsg(Movies(exampleMovies.values.toIndexedSeq))
    }

  }
}

