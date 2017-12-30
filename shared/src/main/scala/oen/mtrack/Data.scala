package oen.mtrack

import derive.key

sealed trait Data

@key("token") case class Token(value: Option[String]) extends Data
@key("credential") case class Credential(name: String, passwd: String) extends Data
@key("register") case class Register(credential: Credential) extends Data

@key("movie") case class Movie(
  id: Int,
  name: String,
  seasons: IndexedSeq[Season],
  poster: String,
  backdrop: String,
  currentSeason: Season = Season()
) extends Data
@key("season") case class Season(season: Int = 1, episode: Int = 1) extends Data
@key("movies") case class Movies(movies: IndexedSeq[Movie]) extends Data

@key("search-movie") case class SearchMovie(
  id: Int,
  name: String,
  poster: Option[String]
) extends Data
@key("search-movies") case class SearchMovies(movies: IndexedSeq[SearchMovie]) extends Data

object Data {
  def toJson(data: Data): String = {
    upickle.default.write(data)
  }

  def fromJson(json: String): Data = {
    upickle.default.read[Data](json)
  }

}
