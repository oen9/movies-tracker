package oen.mtrack.ajax

import oen.mtrack.{Credential, Data, Register, Token}
import org.scalajs.dom.ext.Ajax

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

  def logout(token: Token, onSucced: => Unit, onFailed: => Unit = {}): Unit = token.value.foreach { t =>
    Ajax.post(s"/logout?token=$t", headers = headers).onComplete {
      case Success(_) => onSucced
      case Failure(_) => onFailed
    }
  }
}
