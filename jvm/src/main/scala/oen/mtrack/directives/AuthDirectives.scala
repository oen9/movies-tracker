package oen.mtrack.directives

import akka.actor.ActorRef
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}

import scala.concurrent.Future

trait AuthDirectives {

  def auth(authenticator: String => Future[Option[ActorRef]]): Directive1[ActorRef] =
    parameter('token.?).flatMap {
      case Some(token) =>
        onSuccess(authenticator(token)).flatMap {
          case Some(actorRef) => provide(actorRef)
          case None => reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("none", None))): Directive1[ActorRef]
        }
      case None => reject(AuthenticationFailedRejection(CredentialsMissing, HttpChallenge("none", None))): Directive1[ActorRef]
    }
}

object AuthDirectives extends AuthDirectives
