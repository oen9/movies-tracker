package oen.mtrack.view

import oen.mtrack.SearchMovie
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

class SearchResultDresser {

  def dressSearchResult(movie: SearchMovie)(onClick: Int => Unit): Div = {
    val posterSrc = movie.poster
      .map(i => s"https://image.tmdb.org/t/p/w150$i")
      .getOrElse("/front-res/image-unavailable1.png")

    val searchResultRow = div(cls := "collection-item",
      div(cls := "row",
        div(cls := "col s6", h5(movie.name)),
        div(cls := "col s6", img(src := posterSrc, height := 50))
      )
    ).render
    searchResultRow.onclick = _ => onClick(movie.id)

    searchResultRow
  }
}
