package oen.mtrack.json

import oen.mtrack.Credential

object Data {
  import spray.json._
  import DefaultJsonProtocol._

  implicit val credentialFormat = jsonFormat2(Credential)
}
