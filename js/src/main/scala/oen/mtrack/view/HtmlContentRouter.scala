package oen.mtrack.view

import org.scalajs.dom
import org.scalajs.dom.html

class HtmlContentRouter(hello: HtmlView,
                       signIn: HtmlView
                       ) {

  def getCurrentContent(): html.Div = {
    readHash() match {
      case Some("signin") => signIn.get()
      case _ => hello.get()
    }
  }

  protected def readHash(): Option[String] = {
    val hash = dom.window.location.hash
    if (hash.isEmpty) None else Some(hash.substring(1))
  }
}
