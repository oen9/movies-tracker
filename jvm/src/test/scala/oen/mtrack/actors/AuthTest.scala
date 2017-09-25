package oen.mtrack.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import oen.mtrack.actors.Auth.{RegisterFailed, RegisterSucced, UserRef}
import oen.mtrack.{Credential, Register, Token}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class AuthTest() extends TestKit(ActorSystem("AuthTest")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val testCredential = Credential("test", "test0")

  "An Auth actor" should {

    "respond with token for testCredential" in {
      val auth = system.actorOf(Auth.props)
      auth ! testCredential
      expectMsgPF() { case Token(Some(_)) => }
    }

    "respond with empty token for wrong credential" in {
      val auth = system.actorOf(Auth.props)
      auth ! Credential("test", "testx")
      expectMsg(Token(None))
    }

    "respond with the same token twice" in {
      val auth = system.actorOf(Auth.props)
      val credential = testCredential

      auth ! credential
      val firstResponse: Token = expectMsgPF() { case t @ Token(Some(_)) => t }

      auth ! credential
      val secondResponse: Token = expectMsgPF() { case t @ Token(Some(_)) => t }

      assert(firstResponse == secondResponse)
    }

    "respond with userRef correlated to user for correct token" in {
      val auth = system.actorOf(Auth.props)

      auth ! testCredential
      val token = expectMsgPF() { case t @ Token(Some(_)) => t }

      auth ! token
      val corelatedUserRef = expectMsgClass(classOf[Auth.UserRef])
      corelatedUserRef.ref.foreach(_ ! User.GetName)
      expectMsg(testCredential.name)
    }

    "respond with empty UserRef for wrong token" in {
      val auth = system.actorOf(Auth.props)
      auth ! Token(Some("wrong-token-123"))
      expectMsg(UserRef(None))
    }

    "kill UserActor and delete token after logout" in {
      val auth = system.actorOf(Auth.props)

      auth ! testCredential
      val token = expectMsgPF() { case t @ Token(Some(_)) => t }
      auth ! token
      val userRef = expectMsgClass(classOf[UserRef]).ref.get

      val probe = TestProbe()
      probe.watch(userRef)
      auth ! Auth.Logout(token)

      expectMsg(Auth.LoggedOut)
      probe.expectTerminated(userRef)

      auth ! token
      expectMsg(UserRef(None))
    }

    "register new user" in {
      val auth = system.actorOf(Auth.props)
      val credential = Credential("newUser", "passwd123")

      auth ! Register(credential)
      expectMsg(RegisterSucced)

      auth ! credential
      expectMsgPF() { case t @ Token(Some(_)) => t }
    }

    "reject registration for alredy existing name" in {
      val auth = system.actorOf(Auth.props)
      val credential = testCredential.copy(passwd = "somePasswd")

      auth ! Register(credential)
      expectMsg(RegisterFailed)

      auth ! credential
      expectMsg(Token(None))
    }

  }
}

