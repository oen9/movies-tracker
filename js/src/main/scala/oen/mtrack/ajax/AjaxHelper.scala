package oen.mtrack.ajax

import oen.mtrack.ajax.AjaxHelper.AjaxExceptionHandler
import oen.mtrack._
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.{Ajax, AjaxException}

import scala.util.{Failure, Success}

class AjaxHelper {

  import scala.concurrent.ExecutionContext.Implicits.global

  val headers = Map("Content-type" -> "application/json")

  def signIn(credential: Credential, onSucced: Token => Unit, onFailed: => Unit = {}): Unit = {
    val data = Data.toJson(credential)

    Ajax.post("/login", data, headers = headers).onComplete {
      case Success(v) =>
        Data.fromJson(v.responseText) match {
          case t @ Token(Some(_))  =>
            onSucced(t)

          case e => println("unexpected: " + e)
      }
      case Failure(e) =>
        onFailed
    }
  }

  def signUp(reg: Register, onSucced: => Unit, onFailed: => Unit): Unit = {
    val data = Data.toJson(reg)

    Ajax.post("/register", data, headers = headers).onComplete {
      case Success(_) => onSucced
      case Failure(_) => onFailed
    }
  }

  def logout(token: Token, onSucced: => Unit, onFailed: AjaxExceptionHandler): Unit = token.value.foreach { t =>
    Ajax.post(s"/logout?token=$t", headers = headers).onComplete {
      case Success(_) => onSucced
      case Failure(x: AjaxException) => onFailed(x.xhr)
      case Failure(e) => println(s"unexpected error: $e")
    }
  }

  def loadMovies(token: Token, onSucced: Movies => Unit, onFailed: AjaxExceptionHandler): Unit = token.value.foreach { t =>
    Ajax.get(s"/movies/get-movies?token=$t").onComplete {
      case Success(v) =>
        Data.fromJson(v.responseText) match {
          case m: Movies => onSucced(m)
          case e => println("unexpected: " + e)
        }
      case Failure(x: AjaxException) => onFailed(x.xhr)
      case Failure(e) => println(s"unexpected error: $e")
    }
  }

  def updateCurrentSeason(token: Token, movieId: Int, season: Season, onSucced: => Unit, onFailed: AjaxExceptionHandler): Unit = token.value.foreach { t =>
    val data = Data.toJson(season)
    Ajax.post(s"/movies/update-season/$movieId?token=$t", data, headers = headers).onComplete {
      case Success(v) => onSucced
      case Failure(x: AjaxException) => onFailed(x.xhr)
      case Failure(e) => println(s"unexpected error: $e")
    }
  }

  def addOrUpdate(token: Token, movieId: Int, onSucced: Movie => Unit, onFailed: AjaxExceptionHandler): Unit = token.value.foreach { t =>
    Ajax.post(s"/movies/add-or-update/$movieId?token=$t", headers = headers).onComplete {
      case Success(v) =>
        Data.fromJson(v.responseText) match {
          case m: Movie => onSucced(m)
          case e => println("unexpected: " + e)
        }
      case Failure(x: AjaxException) => onFailed(x.xhr)
      case Failure(e) => println(s"unexpected error: $e")
    }
  }

  def removeMovie(token: Token, movieId: Int, onSucced: => Unit, onFailed: AjaxExceptionHandler): Unit = token.value.foreach { t =>
    Ajax.post(s"/movies/remove/$movieId?token=$t", headers = headers).onComplete {
      case Success(v) => onSucced
      case Failure(x: AjaxException) => onFailed(x.xhr)
      case Failure(e) => println(s"unexpected error: $e")
    }
  }
}

object AjaxHelper {
  type AjaxExceptionHandler = PartialFunction[XMLHttpRequest, Unit]
}
