package oen.mtrack.akkahttpsupport

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{ContentTypes, MediaTypes, MessageEntity}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import oen.mtrack.Data

object UpickleSupport {

  implicit def upickleUnmarshaller: FromEntityUnmarshaller[Data] = Unmarshaller
    .byteStringUnmarshaller
    .forContentTypes(ContentTypes.`application/json`)
    .mapWithCharset {
      case (ByteString.empty, _) => throw Unmarshaller.NoContentException
      case (data, charset) =>
        val decodedStr = data.decodeString(charset.nioCharset().name())
        Data.fromJson(decodedStr)
    }

  implicit def upickleMarshaller: Marshaller[Data, MessageEntity] = Marshaller
    .stringMarshaller(MediaTypes.`application/json`)
    .compose(data => Data.toJson(data))
}
