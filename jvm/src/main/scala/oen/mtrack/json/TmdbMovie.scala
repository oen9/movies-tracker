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

object TmdbMovie {
  implicit val tmdbSeasonFormat = jsonFormat2(TmdbSeason)
  implicit val tmdbMovieFormat = jsonFormat5(TmdbMovie.apply)
}
