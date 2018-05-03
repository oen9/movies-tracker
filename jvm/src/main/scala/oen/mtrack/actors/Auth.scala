package oen.mtrack.actors

import java.util.UUID

import akka.actor.{ActorRef, PoisonPill, Props}
import akka.persistence.RecoveryCompleted
import com.mongodb.BasicDBObject
import oen.mtrack.actors.Auth._
import oen.mtrack.config.ConfigHelper
import oen.mtrack.config.ConfigHelper.dev
import oen.mtrack.json.Data._
import oen.mtrack.{Credential, Token}
import org.mindrot.jbcrypt.BCrypt
import spray.json.DefaultJsonProtocol

class Auth(val persistenceId: String) extends MongoObjectPersistentActor {
  var credentials: Vector[Credential] = Vector(Credential(TEST_NAME, BCrypt.hashpw(TEST_PASSWD, BCrypt.gensalt())))
  var loggeds: Set[Logged] = Set()

  val jsonParsers = Set("registered" -> registeredFormat)

  implicit val profile = ConfigHelper.getProfile(context.system.settings.config)
  dev {
    val newUserActor = context.actorOf(User.props(TEST_NAME), User.name(TEST_NAME))
    val newLogged = Logged(Credential(TEST_NAME, TEST_PASSWD), Token(Some(TEST_NAME)), newUserActor)
    loggeds = loggeds + newLogged
  }

  override def receiveRecover: Receive = {
    case mongoObj: BasicDBObject =>
      readMongoEvent(mongoObj) match {
        case e: Evt => updateState(e)
        case unexpected => log.warning("unexpected recovery from BasicDBObject: {}", unexpected)
      }
    case RecoveryCompleted => // do nothing
    case unexpected => log.warning("unexpected recovery: {}", unexpected)
  }

  override def receiveCommand: Receive = {
    case TryRegister(c) =>
      credentials.find(_.name == c.name) match {
        case Some(_) =>
          sender() ! RegisterFailed
        case None =>
          val toRespond = sender()
          val encryptedCredential = c.copy(passwd = BCrypt.hashpw(c.passwd, BCrypt.gensalt()))
          persistAsJson(Registered(encryptedCredential))(r => {
            updateState(r)
            toRespond ! RegisterSucced
          })
      }

    case Login(c) =>
      credentials.find(_.name == c.name).filter(found => BCrypt.checkpw(c.passwd, found.passwd)) match {
        case Some(cred) =>
          val logged = getLogged(cred)
          sender() ! logged.token
        case None =>
          sender() ! Token(None)
      }
      credentials.find(_.name == c.name).exists(found => BCrypt.checkpw(c.passwd, found.passwd))

    case GetUserRefByToken(t @ Token(Some(_))) =>
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
  }

  def updateState(evt: Evt) =  evt match {
    case Registered(c) => credentials = credentials :+ c
  }

  def getLogged(credential: Credential): Logged = {
    loggeds.find(_.credential == credential).getOrElse {
      val actorName = User.name(s"$persistenceId-${credential.name}")
      val newUserActor = context.actorOf(User.props(credential.name), actorName)
      context.watch(newUserActor)

      val newToken = genToken()
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
  def props(persistenceId: String = name) = Props(new Auth(persistenceId))
  def name = "auth"

  sealed trait Cmd
  case class TryRegister(credential: Credential) extends Cmd
  case class Login(credential: Credential) extends Cmd
  case class GetUserRefByToken(token: Token) extends Cmd
  case class Logout(token: Token) extends Cmd

  sealed trait Evt
  case class Registered(credential: Credential) extends Evt
  implicit val registeredFormat = DefaultJsonProtocol.jsonFormat1(Registered)

  case object LoggedOut
  case object LogoutFailed
  case object RegisterSucced
  case object RegisterFailed

  case class UserRef(ref: Option[ActorRef])
  case class Logged(credential: Credential, token: Token, actorRef: ActorRef)

  val TEST_NAME = "test"
  val TEST_PASSWD = "test0"
}
