package oen.mtrack.materialize

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

class ModalOptions extends js.Object {
  var dismissible: Boolean = true
}

@js.native
trait ModalOperations extends js.Object {
  def modal(action: String): Unit = js.native
  def modal(options: ModalOptions): Unit = js.native
}
