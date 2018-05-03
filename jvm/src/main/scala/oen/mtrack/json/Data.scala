package oen.mtrack.json

import oen.mtrack.{Credential, Movie, Season}

object Data {
  import spray.json._
  import DefaultJsonProtocol._

  implicit val credentialFormat = jsonFormat2(Credential)
  implicit val seasonFormat = jsonFormat2(Season)
  implicit val movieFormat = jsonFormat6(Movie)
}
