package oen.mtrack.view

import oen.mtrack.ajax.AjaxHelper
import oen.mtrack.ajax.AjaxHelper.AjaxExceptionHandler
import oen.mtrack.components.CacheData
import oen.mtrack.{Movie, Season}
import oen.mtrack.materialize.JQueryHelper
import org.scalajs.dom.html.LI

import scalatags.JsDom.all._

class MovieListDresser(jQueryHelper: JQueryHelper, ajaxHelper: AjaxHelper, cacheData: CacheData) {
  def dressMovie(m: Movie, onFailed: AjaxExceptionHandler): LI = {

    val deleteButton = a(cls := "btn-floating btn-large", i(cls := "material-icons", "delete")).render
    val refreshButton = a(cls := "btn-floating btn-large", i(cls := "material-icons", "refresh")).render
    val saveButton = a(cls := "btn-floating btn-large disabled", i(cls := "material-icons", "save")).render

    val movieName = div(cls := "col m6 left-align").render

    val seasonUp = a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_up")).render
    val seasonCounter = h5.render
    val seasonDown = a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_down")).render

    val episodeUp = a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_up")).render
    val episodeCounter = h5.render
    val episodeDown = a(cls := "btn-floating btn-small", i(cls := "material-icons", "keyboard_arrow_down")).render

    val poster = img(height := 100).render
    val backdrop = img.render

    val movieRow = li(
      div(cls := "collapsible-header", style := "display: block",
        div(cls := "row valign-wrapper",
          div(cls := "col m3", poster),
          movieName,
          div(cls := "col m3", i(cls := "material-icons", "visibility_off"))
        )
      ),
      div(cls := "collapsible-body",
        div(cls := "row valign-wrapper",
          div(cls := "col s3", backdrop),
          div(cls := "col s3",
            h6("season"),
            seasonUp,
            seasonCounter,
            seasonDown
          ),
          div(cls := "col s3",
            h6("episode"),
            episodeUp,
            episodeCounter,
            episodeDown
          ),
          div(cls := "col s3",
            deleteButton,
            refreshButton,
            saveButton
          )
        )
      )
    ).render

    def fillMoviesData(toFill: Movie): Unit = {
      val episodesInActiveSeason = toFill.seasons.find(_.season == toFill.currentSeason.season).map(_.episode).getOrElse(0)

      movieName.innerHTML = s"${toFill.name}"
      seasonCounter.innerHTML = s"${toFill.currentSeason.season}/${toFill.seasons.length}"
      episodeCounter.innerHTML = s"${toFill.currentSeason.episode}/$episodesInActiveSeason"

      poster.setAttribute(src.name, s"https://image.tmdb.org/t/p/w150${toFill.poster}")
      backdrop.setAttribute(src.name, s"https://image.tmdb.org/t/p/w300${toFill.backdrop}")

      val seasons = toFill.seasons

      seasonUp.onclick = _ => {
        jQueryHelper.enableElement(saveButton)
        val seasonUp = toFill.seasons.lift(toFill.currentSeason.season).orElse(seasons.headOption).map(_.copy(episode = 1)).getOrElse(Season())
        fillMoviesData(toFill.copy(currentSeason = seasonUp))
      }

      seasonDown.onclick = _ => {
        jQueryHelper.enableElement(saveButton)
        val seasonDown = toFill.seasons.lift(toFill.currentSeason.season - 2).orElse(seasons.lastOption).map(_.copy(episode = 1)).getOrElse(Season())
        fillMoviesData(toFill.copy(currentSeason = seasonDown))
      }

      episodeUp.onclick = _ => {
        jQueryHelper.enableElement(saveButton)
        val incEpNumber = toFill.currentSeason.episode + 1
        val fixedEpNumber = if (incEpNumber > episodesInActiveSeason) 1 else incEpNumber
        val currSeasonWithIncEp = toFill.currentSeason.copy(episode = fixedEpNumber)
        fillMoviesData(toFill.copy(currentSeason = currSeasonWithIncEp))
      }

      episodeDown.onclick = _ => {
        jQueryHelper.enableElement(saveButton)
        val decEpNumber = toFill.currentSeason.episode - 1
        val fixedEpNumber = if (decEpNumber < 1) episodesInActiveSeason else decEpNumber
        val currSeasonWithDecEp = toFill.currentSeason.copy(episode = fixedEpNumber)
        fillMoviesData(toFill.copy(currentSeason = currSeasonWithDecEp))
      }

      saveButton.onclick = _ => {
        ajaxHelper.updateCurrentSeason(cacheData.data.token, m.id, toFill.currentSeason,
          { jQueryHelper.disableElement(saveButton) },
          onFailed)
      }
    }

    fillMoviesData(m)

    deleteButton.onclick = (_) => {
      ajaxHelper.removeMovie(cacheData.data.token, m.id,
        movieRow.parentElement.removeChild(movieRow),
        onFailed
      )
    }

    refreshButton.onclick = _ => {
      def refreshMovie(rcvM: Movie) = {
        fillMoviesData(rcvM)
        jQueryHelper.disableElement(saveButton)
      }

      ajaxHelper.addOrUpdate(cacheData.data.token, m.id, refreshMovie, onFailed)
    }

    movieRow
  }
}
