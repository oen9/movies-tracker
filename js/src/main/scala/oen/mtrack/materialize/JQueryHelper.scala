package oen.mtrack.materialize

import org.scalajs.dom.raw.{Element, VisibilityState}
import org.scalajs.jquery

import scalatags.JsDom.all._

class JQueryHelper {
  def initMaterialize(): Unit = {
    jquery.jQuery(".modal").asInstanceOf[ModalOperations].modal(new ModalOptions { dismissible = false })
    refreshTooltips()
    refreshParallax()
    refreshCollapsaible()
  }

  def refreshTooltips(): Unit = {
    jquery.jQuery(".tooltipped").asInstanceOf[TooltipsOperations].tooltip(new TooltipsOptions)
  }

  def refreshParallax(): Unit = {
    jquery.jQuery(".parallax").asInstanceOf[Parallax].parallax()
  }

  def refreshCollapsaible(): Unit = {
    jquery.jQuery(".collapsible").asInstanceOf[Collapsible].collapsible()
  }

  def hideElement(element: Element): Unit = {
    element.setAttribute(hidden.v, VisibilityState.hidden.toString)
  }

  def showElement(element: Element): Unit = {
    element.removeAttribute(hidden.v)
  }
}
