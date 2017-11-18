package oen.mtrack.materialize

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

class TooltipsOptions extends js.Object {
  var delay: Int = 50
}

@js.native
trait TooltipsOperations extends js.Object {
  def tooltip(options: TooltipsOptions): Unit = js.native
}
