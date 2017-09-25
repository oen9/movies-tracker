package oen.mtrack.view

import org.scalajs.dom.html
import scalatags.JsDom.all._

class Hello extends HtmlView {

  override def get(): html.Div =
    div(cls := "container",
      h2(cls := "header", "How to start?"),
      ol(
        li("Sign up with FREE account WITHOUT any confirmation (email etc)"),
        li("Sign in"),
        li("Use it! Track your serials/series/movies in way you like")
      )
    ).render
}
