package oen.mtrack.view

import oen.mtrack.components.{CacheData, DashboardComp}
import org.scalajs.dom
import org.scalajs.dom.html

import scalatags.JsDom.all._

class Dashboard(val cacheData: CacheData, dashboardComp: DashboardComp) extends HtmlView {

  override def get(): html.Div = {
    val username = cacheData.data.username.getOrElse("unknown")
    val searchResults = ul(cls := "collection scrollable-mini").render

    var intervalId: Option[Int] = None

    dashboardComp.searcher.onkeyup = _ => {
      intervalId.foreach(dom.window.clearInterval)

      intervalId = Some(dom.window.setInterval(() => {
        intervalId.foreach(dom.window.clearInterval)
        println(dashboardComp.searcher.value)

        searchResults.innerHTML = ""
        val sValue = dashboardComp.searcher.value
        for (pre <- 1 to sValue.length) {
          searchResults.appendChild(
            div(cls := "collection-item",
              div(cls := "row",
                div(cls := "col s6", h5("Mr. Robot" + sValue.substring(0, pre))),
                div(cls := "col s6", img(src := "https://image.tmdb.org/t/p/w150/qE0t9rlClIReax0d5tr3j300wUt.jpg", height := 50))
              )
            ).render
          )
        }

      }, 500))
    }

    div(cls := "container center",
      h5(cls := "header", s"Logged as $username"),
      div(cls := "input-field",
        i(cls := "material-icons prefix", "search"),
        dashboardComp.searcher,
        label(`for` := "search-movie-input", "search movie")
      ),
      dashboardComp.searchResults,
      dashboardComp.moviesList
    ).render
  }
}
