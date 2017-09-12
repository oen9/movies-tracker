package oen.mtrack.view

import org.scalajs.dom.html

import scalatags.JsDom.all._

trait HtmlView {
  def get(): html.Div = div(cls := "error", "in build").render
}
