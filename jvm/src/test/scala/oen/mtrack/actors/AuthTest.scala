package oen.mtrack.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import oen.mtrack.actors.Auth.{RegisterFailed, RegisterSucced, UserRef}
import oen.mtrack.{Credential, Token}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class AuthTest() extends TestKit(ActorSystem("AuthTest")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val testCredential = Credential("test", "test0")

  "An Auth actor" should {

    "respond with token for testCredential" in {
      val auth = system.actorOf(Auth.props("authTokenTest"))
      auth ! Auth.Login(testCredential)
      expectMsgPF() { case Token(Some(_)) => }
    }

    "respond with empty token for wrong credential" in {
      val auth = system.actorOf(Auth.props("authEmptyToken"))
      auth ! Auth.Login(Credential("test", "testx"))
      expectMsg(Token(None))
    }

    "respond with the same token twice" in {
      val auth = system.actorOf(Auth.props("authSameToken"))
      val credential = testCredential

      auth ! Auth.Login(credential)
      val firstResponse: Token = expectMsgPF() { case t @ Token(Some(_)) => t }

      auth ! Auth.Login(credential)
      val secondResponse: Token = expectMsgPF() { case t @ Token(Some(_)) => t }

      assert(firstResponse == secondResponse)
    }

    "respond with userRef correlated to user for correct token" in {
      val auth = system.actorOf(Auth.props("authCorellatedToken"))

      auth ! Auth.Login(testCredential)
      val token = expectMsgPF() { case t @ Token(Some(_)) => t }

      auth ! Auth.GetUserRefByToken(token)
      val corelatedUserRef = expectMsgClass(classOf[Auth.UserRef])
      corelatedUserRef.ref.foreach(_ ! User.GetName)
      expectMsg(testCredential.name)
    }

    "respond with empty UserRef for wrong token" in {
      val auth = system.actorOf(Auth.props("authWrongToken"))
      auth ! Auth.GetUserRefByToken(Token(Some("wrong-token-123")))
      expectMsg(UserRef(None))
    }

    "kill UserActor and delete token after logout" in {
      val auth = system.actorOf(Auth.props("authDeleteToken"))

      auth ! Auth.Login(testCredential)
      val token = expectMsgPF() { case t @ Token(Some(_)) => t }
      auth ! Auth.GetUserRefByToken(token)
      val userRef = expectMsgClass(classOf[UserRef]).ref.get

      val probe = TestProbe()
      probe.watch(userRef)
      auth ! Auth.Logout(token)

      expectMsg(Auth.LoggedOut)
      probe.expectTerminated(userRef)

      auth ! Auth.GetUserRefByToken(token)
      expectMsg(UserRef(None))
    }

    "register new user" in {
      val auth = system.actorOf(Auth.props("authRegisterNew"))
      val credential = Credential("newUser", "passwd123")

      auth ! Auth.TryRegister(credential)
      expectMsg(RegisterSucced)

      auth ! Auth.Login(credential)
      expectMsgPF() { case t @ Token(Some(_)) => t }
    }

    "reject registration for already existing name" in {
      val auth = system.actorOf(Auth.props("authRegisterExisting"))
      val credential = testCredential.copy(passwd = "somePasswd")

      auth ! Auth.TryRegister(credential)
      expectMsg(RegisterFailed)

      auth ! Auth.Login(credential)
      expectMsg(Token(None))
    }

  }
}

