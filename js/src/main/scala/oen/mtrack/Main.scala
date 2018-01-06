package oen.mtrack

import oen.mtrack.ajax.AjaxHelper
import oen.mtrack.components._
import oen.mtrack.materialize.JQueryHelper
import oen.mtrack.view._
import org.scalajs.dom.html.Element

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("oen.mtrack")
object Main {

  @JSExport
  def main(header: Element, main: Element, footer: Element): Unit = {
    val jQueryHelper = new JQueryHelper
    val ajaxHelper = new AjaxHelper
    val cacheData = new CacheData()
    val htmlDresser = new MovieListDresser(jQueryHelper, ajaxHelper, cacheData)
    val searchResultDresser = new SearchResultDresser
    val localStorageService = new LocalStorageService(cacheData)
    val staticComponents = new StaticComponents

    localStorageService.restoreSaved()

    val componentsLogic = new ComponentsLogic(
      staticComponents = staticComponents,
      cacheData = cacheData,
      jQueryHelper = jQueryHelper,
      ajaxHelper = ajaxHelper,
      localStorageService = localStorageService,
      movieListDresser = htmlDresser,
      searchResultDresser = searchResultDresser
    )

    val htmlContentRouter = new HtmlContentRouter(
      componentsLogic = componentsLogic,
      hello = new Hello with ParallaxView,
      signIn = new SignIn(staticComponents.signIn) with ParallaxView,
      signUp = new SignUp(staticComponents.signUp) with ParallaxView,
      dashboard = new Dashboard(cacheData, staticComponents.dashboard) with ParallaxView with OnlyLoggedView
    )

    val mc = new MainContent(htmlContentRouter, jQueryHelper, staticComponents.header)
    mc.init(header, main, footer)

    componentsLogic.init()
  }
}
