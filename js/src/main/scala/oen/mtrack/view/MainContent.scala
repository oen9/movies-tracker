package oen.mtrack.view

import oen.mtrack.components.HeaderComp
import oen.mtrack.materialize.JQueryHelper
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.HashChangeEvent

import scalatags.JsDom.all._
import scalatags.JsDom.tags2

class MainContent(htmlContentRouter: HtmlContentRouter,
                  jQueryHelper: JQueryHelper,
                  headerComp: HeaderComp) {

  def init(header: html.Element, main: html.Element, footer: html.Element): Unit = {
    val headerC = initHeader()
    header.appendChild(headerC)

    val mainC = initMain()
    main.appendChild(mainC)

    val footerC = initFooter()
    footer.appendChild(footerC)

    dom.window.onhashchange = (e: HashChangeEvent) => {
      onHashChange(main)
    }
  }

  protected def initHeader(): html.Element = {
    tags2.nav(
      div(cls := "nav-wrapper",
        a(cls := "brand-logo center", href := "#", "Movies-Tracker"),
        ul(cls := "right",
          headerComp.signin,
          headerComp.signUp,
          headerComp.dashboard,
          headerComp.logout
        )
      )
    ).render
  }

  protected def initMain(): html.Div = {
    htmlContentRouter.getCurrentContent()
  }

  protected def initFooter(): html.Div = {
    div(cls := "footer-copyright",
      div(cls := "container",
        "© 2017 oen",
        a(cls := "grey-text text-lighten-4 right", target := "_blank", href := "https://github.com/oen9/movies-tracker", "github")
      )
    ).render
  }

  protected def onHashChange(main: html.Element): Unit = {
    main.innerHTML = ""
    val mainContent = initMain()
    main.appendChild(mainContent)
    jQueryHelper.refreshParallax()
  }
}
