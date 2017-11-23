package oen.mtrack.config

import com.typesafe.config.Config

sealed trait Profile
case object Prod extends Profile
case object Dev extends Profile

object ConfigHelper {
  def getProfile(config: Config): Profile = config.getString("profile") match {
    case "dev" => Dev
    case _ => Prod
  }

  def dev(f: => Unit)(implicit profile: Profile): Unit = profile match {
    case Dev => f
    case Prod =>
  }
}
