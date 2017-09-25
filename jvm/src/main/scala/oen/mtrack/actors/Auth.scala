package oen.mtrack.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import oen.mtrack.actors.Auth._
import oen.mtrack.{Credential, Register, Token}

class Auth extends Actor {

  var credentials: Vector[Credential] = Vector(Credential("test", "test0"))
  var loggeds: Set[Logged] = Set()

  override def receive = {
    case c: Credential if credentials.contains(c) =>
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
          credentials = credentials :+ c
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
}
