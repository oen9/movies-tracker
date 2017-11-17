package oen.mtrack.components

import oen.mtrack.Token

class CacheData(var data: ImmutableCacheData = ImmutableCacheData())

case class ImmutableCacheData(
  username: Option[String] = None,
  token: Token = Token(None)
)
