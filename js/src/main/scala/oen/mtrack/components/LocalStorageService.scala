package oen.mtrack.components

import oen.mtrack.Token
import org.scalajs.dom

class LocalStorageService(cacheData: CacheData) {

  def restoreSaved() = {
    import LocalStorageService._
    for (
      username <- Option(dom.window.localStorage.getItem(USERNAME_KEY));
      token <- Option(dom.window.localStorage.getItem(TOKEN_KEY))
    ) yield {
      cacheData.data = cacheData.data.copy(username = Some(username), token = Token(Some(token)))
    }
  }

  def save() = {
    import LocalStorageService._
    for (
      username <- cacheData.data.username;
      token <- cacheData.data.token.value
    ) yield {
      dom.window.localStorage.setItem(USERNAME_KEY, username)
      dom.window.localStorage.setItem(TOKEN_KEY, token)
    }
  }

  def clearSaved() = {
    import LocalStorageService._
    dom.window.localStorage.removeItem(USERNAME_KEY)
    dom.window.localStorage.removeItem(TOKEN_KEY)
  }
}

object LocalStorageService {
  final val USERNAME_KEY = "username"
  final val TOKEN_KEY = "token"
}
