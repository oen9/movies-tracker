package oen.mtrack.view

import oen.mtrack.components.CacheData
import org.scalajs.dom.html

import scalatags.JsDom.all._

class Dashboard(cacheData: CacheData) extends HtmlView {

  override def get(): html.Div = {
    val username = cacheData.data.username.getOrElse("unknown")

    div(cls := "container",
      h2(cls := "header", s"Hello $username!"),
      p("Have a nice day!")
    ).render
  }
}
