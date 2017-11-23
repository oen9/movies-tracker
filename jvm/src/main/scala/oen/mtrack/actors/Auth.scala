package oen.mtrack.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import oen.mtrack.actors.Auth._
import oen.mtrack.config.ConfigHelper
import oen.mtrack.config.ConfigHelper.dev
import oen.mtrack.{Credential, Register, Token}
import org.mindrot.jbcrypt.BCrypt

class Auth extends Actor {
  var credentials: Vector[Credential] = Vector(Credential(TEST_NAME, BCrypt.hashpw(TEST_PASSWD, BCrypt.gensalt())))
  var loggeds: Set[Logged] = Set()

  implicit val profile = ConfigHelper.getProfile(context.system.settings.config)
  dev {
    val newUserActor = context.actorOf(User.props(TEST_NAME), User.name(TEST_NAME))
    val newLogged = Logged(Credential(TEST_NAME, TEST_PASSWD), Token(Some(TEST_NAME)), newUserActor)
    loggeds = loggeds + newLogged
  }

  override def receive = {
    case c: Credential if credentials.find(_.name == c.name).exists(found => BCrypt.checkpw(c.passwd, found.passwd)) =>
      val logged = getLogged(c)
      sender() ! logged.token

    case t @ Token(Some(_)) =>
      sender ! loggeds
        .find(_.token == t)
        .map(l => UserRef(Some(l.actorRef)))
        .getOrElse(UserRef(None))

    case Logout(t) =>
      sender() ! loggeds
        .find(_.token == t)
        .map(logged => {
          logged.actorRef ! PoisonPill
          context.unwatch(logged.actorRef)
          loggeds = loggeds - logged
          LoggedOut
        })
        .getOrElse(LogoutFailed)

    case Register(c) =>
      sender() ! credentials
        .find(_.name == c.name)
        .map(_ => RegisterFailed)
        .getOrElse({
          credentials = credentials :+ c.copy(passwd = BCrypt.hashpw(c.passwd, BCrypt.gensalt()))
          RegisterSucced
        })

    case _: Credential => sender() ! Token(None)
    case _ =>
  }

  def getLogged(credential: Credential): Logged = {
    loggeds.find(_.credential == credential).getOrElse {
      val newToken = genToken()
      val newUserActor = context.actorOf(User.props(credential.name), User.name(credential.name))
      context.watch(newUserActor)
      val newLogged = Logged(credential, newToken, newUserActor)

      loggeds = loggeds + newLogged
      newLogged
    }
  }

  def genToken(): Token = {
    val tokenValue = UUID.randomUUID().toString.replaceAll("-", "")
    Token(Some(tokenValue))
  }

}

object Auth {
  def props = Props(new Auth)
  def name = "auth"

  case class Logout(token: Token)
  case object LoggedOut
  case object LogoutFailed

  case object RegisterSucced
  case object RegisterFailed

  case class UserRef(ref: Option[ActorRef])
  case class Logged(credential: Credential, token: Token, actorRef: ActorRef)

  val TEST_NAME = "test"
  val TEST_PASSWD = "test0"
}
