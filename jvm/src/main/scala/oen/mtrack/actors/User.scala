package oen.mtrack.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import oen.mtrack.actors.User._
import oen.mtrack.json.TmdbMovie
import oen.mtrack.{Movie, Movies, Season}

class User(name: String, var movies: Map[Int, Movie]) extends Actor {
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val tmdbApiKey: String = context.system.settings.config.getString("tmdb.api.key")

  override def receive = {
    case GetName =>
      sender() ! name

    case GetMovies =>
      sender() ! Movies(movies.values.toVector)

    case RemoveMovie(id) =>
      movies = movies - id
      sender() ! Success

    case UpdateCurrentSeason(id, season) =>
      movies.get(id).foreach { m =>
        movies = movies + (id -> m.copy(currentSeason = season))
      }
      sender() ! Success

    case AddOrUpdateMovie(id) =>
      val toRespond = sender()
      fetchMovie(id, toRespond)

    case ToAdd(toResponse, m) =>
      val newOrUpdatedMovie = movies.get(m.id).fold(m)(oldM => m.copy(currentSeason = oldM.currentSeason))
      movies = movies + (m.id -> newOrUpdatedMovie)
      toResponse ! newOrUpdatedMovie
  }

  def fetchMovie(id: Int, toRespond: ActorRef): Unit = {
    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import akka.pattern.pipe
    import context.dispatcher

    val toAdd = for {
      request <- Http(context.system).singleRequest(HttpRequest(uri = s"https://api.themoviedb.org/3/tv/$id?api_key=$tmdbApiKey"))
      tmdbMovie <- Unmarshal(request.entity).to[TmdbMovie]
    } yield {
      val movie = Movie(
        id = tmdbMovie.id,
        name = tmdbMovie.name,
        poster = tmdbMovie.poster_path,
        backdrop =  tmdbMovie.backdrop_path,
        seasons = tmdbMovie.seasons.filterNot(_.season_number == 0).map(s => Season(s.season_number, s.episode_count))
      )
      ToAdd(toRespond, movie)
    }

    toAdd.pipeTo(self)
  }
}

object User {
  def props(name: String, movies: Map[Int, Movie] = Map()) = Props(new User(name, movies))
  def name(username: String) = s"user-$username"

  trait cmd
  case object GetName extends cmd
  case object GetMovies extends cmd
  case object Success extends cmd
  case class AddOrUpdateMovie(id: Int) extends cmd
  case class RemoveMovie(id: Int) extends cmd
  case class UpdateCurrentSeason(id: Int, season: Season) extends cmd

  case class ToAdd(toResponse: ActorRef, movie: Movie)
}
