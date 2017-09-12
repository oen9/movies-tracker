package oen.mtrack.materialize

import org.scalajs.jquery

class JQueryHelper {
  def initMaterialize(): Unit = {
    jquery.jQuery(".modal").asInstanceOf[ModalOperations].modal(new ModalOptions { dismissible = false })
    refreshTooltips()
    refreshParallax()
  }

  def refreshTooltips(): Unit = {
    jquery.jQuery(".tooltipped").asInstanceOf[TooltipsOperations].tooltip(new TooltipsOptions)
  }

  def refreshParallax(): Unit = {
    jquery.jQuery(".parallax").asInstanceOf[Parallax].parallax()
  }
}
