package oen.mtrack

import oen.mtrack.materialize.JQueryHelper
import oen.mtrack.view.{Hello, HtmlContentRouter, MainContent, SignIn}
import org.scalajs.dom.html.Element

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("oen.mtrack")
object Main {

  @JSExport
  def main(header: Element, main: Element, footer: Element): Unit = {
    val jQueryHelper = new JQueryHelper

    val htmlContentRouter = new HtmlContentRouter(
      hello = new Hello,
      signIn = new SignIn
    )

    val mc = new MainContent(htmlContentRouter, jQueryHelper)
    mc.init(header, main, footer)

    jQueryHelper.initMaterialize()
  }
}
