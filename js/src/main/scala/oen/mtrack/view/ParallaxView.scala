package oen.mtrack.view

import org.scalajs.dom.html

import scalatags.JsDom.all._

trait ParallaxView extends HtmlView {
  override def get(): html.Div =
    div(
      div(cls := "parallax-container",
        div(cls := "parallax", img(src := "front-res/hello1.jpeg"))
      ),
      super.get(),
      div(cls := "parallax-container",
        div(cls := "parallax", img(src := "front-res/hello2.jpeg"))
      ),
    ).render
}
