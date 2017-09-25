package oen.mtrack.actors

import akka.actor.{Actor, Props}
import oen.mtrack.actors.User.GetName

class User(name: String) extends Actor {
  override def receive = {
    case GetName =>
      sender() ! name
  }
}

object User {
  def props(name: String) = Props(new User(name))
  def name(username: String) = s"user-$username"

  case object GetName
}
