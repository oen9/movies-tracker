package oen.mtrack.json

import spray.json._
import DefaultJsonProtocol._

case class TmdbMovie(
  id: Int,
  name: String,
  backdrop_path: String,
  poster_path: String,
  seasons: IndexedSeq[TmdbSeason]
)

case class TmdbSeason(episode_count: Int, season_number: Int)

case class TmdbSearchMovie(
  id: Int,
  name: String,
  poster_path: Option[String]
)
case class TmdbSearchResult(results: IndexedSeq[TmdbSearchMovie])

object TmdbMovie {
  implicit val tmdbSeasonFormat = jsonFormat2(TmdbSeason)
  implicit val tmdbMovieFormat = jsonFormat5(TmdbMovie.apply)
  implicit val tmdbSearchMovie = jsonFormat3(TmdbSearchMovie)
  implicit val tmdbSearchResult = jsonFormat1(TmdbSearchResult)
}
