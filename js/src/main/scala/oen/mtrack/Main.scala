package oen.mtrack

import oen.mtrack.components.{CacheData, ComponentsLogic, StaticComponents}
import oen.mtrack.materialize.JQueryHelper
import oen.mtrack.view._
import org.scalajs.dom.html.Element

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("oen.mtrack")
object Main {

  @JSExport
  def main(header: Element, main: Element, footer: Element): Unit = {
    val jQueryHelper = new JQueryHelper
    val cacheData = new CacheData
    val staticComponents = new StaticComponents

    val htmlContentRouter = new HtmlContentRouter(
      hello = new Hello with ParallaxView,
      signIn = new SignIn with ParallaxView
    )

    val mc = new MainContent(htmlContentRouter, jQueryHelper)
    mc.init(header, main, footer)

    val componentsLogic = new ComponentsLogic(
      staticComponents = staticComponents,
      cacheData = cacheData,
      jQueryHelper = jQueryHelper
    )

    componentsLogic.init()
  }
}
