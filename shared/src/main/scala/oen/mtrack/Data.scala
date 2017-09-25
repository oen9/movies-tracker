package oen.mtrack

import derive.key

sealed trait Data

@key("token") case class Token(value: Option[String]) extends Data
@key("credential") case class Credential(name: String, passwd: String) extends Data
@key("register") case class Register(credential: Credential) extends Data

object Data {
  def toJson(data: Data): String = {
    upickle.default.write(data)
  }

  def fromJson(json: String): Data = {
    upickle.default.read[Data](json)
  }

}
