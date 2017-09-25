package oen.mtrack.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class UserTest() extends TestKit(ActorSystem("UserTest")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An User actor" should {

    "return name" in {
      val testName = "test321"
      val user = system.actorOf(User.props(testName))
      user ! User.GetName
      expectMsg(testName)
    }

  }
}

