package oen.mtrack.ajax

import oen.mtrack.{Credential, Data, Token}
import org.scalajs.dom.ext.Ajax

import scala.util.{Failure, Success}

class AjaxHelper {
  import scala.concurrent.ExecutionContext.Implicits.global

  val headers = Map("Content-type" -> "application/json")

  def signIn(credential: Credential, onSucced: Token => Unit, onUnauth: => Unit = {}): Unit = {
    val data = Data.toJson(credential)

    Ajax.post("/login", data, headers = headers).onComplete {
      case Success(v) =>
        Data.fromJson(v.responseText) match {
          case t @ Token(Some(_))  =>
            onSucced(t)

          case e => println("unexpected: " + e)
      }
      case Failure(e) =>
        onUnauth
    }
  }
}
