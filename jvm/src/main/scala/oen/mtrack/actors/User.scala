package oen.mtrack.actors

import java.net.URLEncoder

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.persistence.RecoveryCompleted
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.mongodb.BasicDBObject
import oen.mtrack._
import oen.mtrack.actors.User._
import oen.mtrack.json.Data._
import oen.mtrack.json.{TmdbMovie, TmdbSearchResult}
import spray.json.DefaultJsonProtocol._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

class User(name: String, var movies: Map[Int, Movie]) extends MongoObjectPersistentActor {
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val tmdbApiKey: String = context.system.settings.config.getString("tmdb.api.key")

  override def persistenceId: String = name

  override def jsonParsers: Set[(String, RootJsonFormat[_])] =
    Set(
      "movieAdded" -> movieAddedFormat,
      "movieRemoved" -> movieRemovedFormat,
      "currentSeasonUpdated" -> currentSeasonUpdatedFormat
    )

  override def receiveRecover: Receive = {
    case mongoObj: BasicDBObject =>
      readMongoEvent(mongoObj) match {
        case e: Evt => updateState(e)
        case unexpected => log.warning("unexpected recovery from BasicDBObject: {}", unexpected)
      }
    case RecoveryCompleted => // do nothing
    case unexpected => log.warning("unexpected recovery: {}", unexpected)
  }

  override def receiveCommand: Receive = {
    case GetName =>
      sender() ! name

    case GetMovies =>
      sender() ! Movies(movies.values.toVector)

    case Search(query) =>
      val toRespond = sender()
      search(query, toRespond)

    case AddOrUpdateMovie(id) =>
      val toRespond = sender()
      fetchMovie(id, toRespond)

    case ToAdd(toResponse, m) =>
      val newOrUpdatedMovie = movies.get(m.id).fold(m)(oldM => m.copy(currentSeason = oldM.currentSeason))
      persistAsJson(MovieAdded(newOrUpdatedMovie))(ma => {
        updateState(ma)
        toResponse ! ma.movie
      })

    case RemoveMovie(id) =>
      val toRespond = sender()
      persistAsJson(MovieRemoved(id))(removed => {
        updateState(removed)
        toRespond ! Success
      })

    case UpdateCurrentSeason(id, season) =>
      val toRespond = sender()
      persistAsJson(CurrentSeasonUpdated(id, season))(season => {
        updateState(season)
        toRespond ! Success
      })
  }

  def updateState(evt: Evt) = evt match {
    case added: MovieAdded =>
      movies = movies + (added.movie.id -> added.movie)
    case removed: MovieRemoved =>
      movies = movies - removed.id
    case CurrentSeasonUpdated(id, season) =>
      movies.get(id).foreach { m =>
        movies = movies + (id -> m.copy(currentSeason = season))
      }
  }

  def fetchMovie(id: Int, toRespond: ActorRef): Unit = {
    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import akka.pattern.pipe
    import context.dispatcher

    val toAdd = for {
      request <- Http(context.system).singleRequest(HttpRequest(uri = s"https://api.themoviedb.org/3/tv/$id?api_key=$tmdbApiKey"))
      tmdbMovie <- Unmarshal(request.entity).to[TmdbMovie]
    } yield {
      val seasons = tmdbMovie.seasons.filterNot(_.season_number == 0).map(s => Season(s.season_number, s.episode_count))
      val movie = Movie(
        id = tmdbMovie.id,
        name = tmdbMovie.name,
        poster = tmdbMovie.poster_path,
        backdrop =  tmdbMovie.backdrop_path,
        seasons = seasons,
        currentSeason = seasons.headOption.map(_.copy(episode = 1)).getOrElse(Season())
      )
      ToAdd(toRespond, movie)
    }

    toAdd.pipeTo(self)
  }

  def search(query: String, toRespond: ActorRef): Unit = {
    import TmdbMovie._
    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import akka.pattern.pipe
    import context.dispatcher

    val encodedQuery = URLEncoder.encode(query, "UTF-8")
    val results = for {
      request <- Http(context.system).singleRequest(HttpRequest(uri = s"https://api.themoviedb.org/3/search/tv?api_key=$tmdbApiKey&query=$encodedQuery"))
      tmdbSearchResults <- Unmarshal(request.entity).to[TmdbSearchResult]
    } yield {
      tmdbSearchResults.results.map { tmdbMovie =>
        SearchMovie(
          id = tmdbMovie.id,
          name = tmdbMovie.name,
          poster = tmdbMovie.poster_path
        )
      }
    }

    results.map(SearchMovies).pipeTo(toRespond)
  }
}

object User {
  def props(name: String, movies: Map[Int, Movie] = Map()) = Props(new User(name, movies))
  def name(username: String) = s"user-$username"

  sealed trait Cmd
  case object GetName extends Cmd
  case object GetMovies extends Cmd
  case class AddOrUpdateMovie(id: Int) extends Cmd
  case class RemoveMovie(id: Int) extends Cmd
  case class UpdateCurrentSeason(id: Int, season: Season) extends Cmd
  case class Search(query: String) extends Cmd

  sealed trait Evt
  case class MovieAdded(movie: Movie) extends Evt
  case class MovieRemoved(id: Int) extends Evt
  case class CurrentSeasonUpdated(id: Int, season: Season) extends Evt

  implicit val currentSeasonUpdatedFormat = DefaultJsonProtocol.jsonFormat2(CurrentSeasonUpdated)
  implicit val movieRemovedFormat = DefaultJsonProtocol.jsonFormat1(MovieRemoved)
  implicit val movieAddedFormat = DefaultJsonProtocol.jsonFormat1(MovieAdded)

  case class ToAdd(toResponse: ActorRef, movie: Movie)
  case object Success
}
