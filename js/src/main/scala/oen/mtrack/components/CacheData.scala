package oen.mtrack.components

import oen.mtrack.Token

class CacheData(
  var username: Option[String] = None,
  var token: Token = Token(None)
)
