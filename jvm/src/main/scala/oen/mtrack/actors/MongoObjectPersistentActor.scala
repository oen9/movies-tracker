package oen.mtrack.actors

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import com.mongodb.BasicDBObject
import com.mongodb.casbah.commons.MongoDBObject
import spray.json.{JsonReader, RootJsonFormat}
import spray.json._

trait MongoObjectPersistentActor extends PersistentActor with ActorLogging {

  def jsonParsers: Set[(String, RootJsonFormat[_])]
  def getJsonParserName[T: JsonReader](implicit jsonReader: JsonReader[T]): Option[String] = jsonParsers.find(p => p._2 == jsonReader).map(_._1)
  def getJsonParser(name: String): Option[JsonReader[_]] = jsonParsers.find(p => p._1 == name).map(_._2)

  def persistAsJson[A](event: A)(handler: A => Unit)(implicit rootJsonFormat: RootJsonFormat[A]): Unit = {
    val mongoObj = getJsonParserName[A] match {
      case Some(parserType) =>
        MongoDBObject("type" -> parserType, "value" -> MongoDBObject(event.toJson.compactPrint))
      case None =>
        log.warning("jsonParserName for [{}] not found. Saving as binary with [UNKNOWN] type", event.getClass)
        MongoDBObject("type" -> "UNKNOWN", "value" -> event)
    }

    super.persist(mongoObj)(_ => handler(event))
  }

  def readMongoEvent(mongoJson: BasicDBObject): Any = {
    val expectedType = mongoJson.getString("type")
    getJsonParser(expectedType) match {
      case Some(jsonReader) =>
        mongoJson.get("value").asInstanceOf[BasicDBObject].toJson.parseJson.convertTo(jsonReader)
      case None =>
        log.warning("unknown jsonParser for type [{}]. Loading as binary", expectedType)
        mongoJson.get("value")
    }
  }

}
