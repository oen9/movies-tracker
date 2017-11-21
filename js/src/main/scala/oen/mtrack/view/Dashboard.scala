package oen.mtrack.view

import oen.mtrack.components.CacheData
import org.scalajs.dom.html

import scalatags.JsDom.all._

class Dashboard(val cacheData: CacheData) extends HtmlView {

  override def get(): html.Div = {
    val username = cacheData.data.username.getOrElse("unknown")

    div(cls := "container center",
      h5(cls := "header", s"Logged as $username"),
      ul(cls := "collapsible popout", attr("data-collapsible") := "expandable",
        li(
          div(cls := "collapsible-header", style := "display: block",
            div(cls := "row valign-wrapper",
              div(cls := "col m3", img(src := "https://image.tmdb.org/t/p/w150/qE0t9rlClIReax0d5tr3j300wUt.jpg", height := 100)),
              div(cls := "col m6 left-align", "Mr. Robot"),
              div(cls := "col m3", i(cls := "material-icons", "visibility_off"))
            )
          ),
          div(cls := "collapsible-body",
            div(cls := "row valign-wrapper",
              div(cls := "col s3", img(cls := "responsive-img", src := "https://image.tmdb.org/t/p/w300/toZQ9IN51cQMzy6fruBZ6024No3.jpg")),
              div(cls := "col s3",
                h6("season"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_up")),
                h5("3/3"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_down"))
              ),
              div(cls := "col s3",
                h6("episode"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_up")),
                h5("5/10"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_down"))
              ),
              div(cls := "col s3",
                a(cls := "btn-floating btn-large", i(cls := "material-icons", "delete")),
                a(cls := "btn-floating btn-large", i(cls := "material-icons", "refresh"))
              )
            )
          )
        ),

        li(
          div(cls := "collapsible-header", style := "display: block",
            div(cls := "row valign-wrapper",
              div(cls := "col s3", img(src := "https://image.tmdb.org/t/p/w150/ydmfheI5cJ4NrgcupDEwk8I8y5q.jpg", height := 100)),
              div(cls := "col s6 left-align", "Dexter"),
              div(cls := "col s3", i(cls := "material-icons", "beenhere"))
            )
          ),
          div(cls := "collapsible-body",
            div(cls := "row valign-wrapper",
              div(cls := "col s3", img(cls := "responsive-img", src := "https://image.tmdb.org/t/p/w300/5m05BIoMHgTd4zvJ5OBh7gZFGWV.jpg")),
              div(cls := "col s3",
                h6("season"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_up")),
                h5("3/3"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_down"))
              ),
              div(cls := "col s3",
                h6("episode"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_up")),
                h5("5/10"),
                a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_down"))
              ),
              div(cls := "col s3",
                a(cls := "btn-floating btn-large", i(cls := "material-icons", "delete")),
                a(cls := "btn-floating btn-large", i(cls := "material-icons", "refresh"))
              )
            )
          )
        )

      )
    ).render
  }
}
