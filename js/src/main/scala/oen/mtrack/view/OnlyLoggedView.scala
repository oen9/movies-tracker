package oen.mtrack.view

import oen.mtrack.components.CacheData
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

trait OnlyLoggedView extends HtmlView {

  def cacheData: CacheData

  override def get(): Div = {
    cacheData.data.token.value match {
      case Some(_) =>
        super.get()

      case None =>
        dom.window.location.hash = "signin"
        div("access denied").render
    }
  }
}
