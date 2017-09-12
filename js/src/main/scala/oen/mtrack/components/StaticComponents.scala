package oen.mtrack.components

import org.scalajs.dom.html.{Anchor, Div, Input}

import scalatags.JsDom.all._

case class StaticComponents (
  progressbar: Div = div(cls := "progress", div(cls := "indeterminate")).render,
)
