package oen.mtrack.view

import oen.mtrack.SearchMovie
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

class SearchResultDresser {

  def dressSearchResult(movie: SearchMovie)(onClick: Int => Unit): Div = {
    val searchResultRow = div(cls := "collection-item",
      div(cls := "row",
        div(cls := "col s6", h5(movie.name)),
        div(cls := "col s6", img(src := ImageHelper.getSrcW300(movie.poster), height := 50))
      )
    ).render
    searchResultRow.onclick = _ => onClick(movie.id)

    searchResultRow
  }
}
