package forum.models

import java.sql.Timestamp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pl.iterators.kebs.json.KebsSpray
import spray.json._

import scala.language.implicitConversions
import scala.util.Try

trait Protocols extends SprayJsonSupport with DefaultJsonProtocol with KebsSpray {

  implicit val printer = PrettyPrinter
  implicit val timestampFormat: JsonFormat[Timestamp] = jsonFormat[Timestamp](TimestampReader, TimestampWriter)

  implicit val uuidFormat: JsonFormat[UUID] {} = new JsonFormat[UUID] {
    override def write(obj: UUID): JsValue = JsString(obj.toString)

    override def read(json: JsValue): UUID = json match {
      case JsString(uuid) => Try(UUID.fromString(uuid)).getOrElse(deserializationError("Expected UUID format"))
      case _              => deserializationError("Expected UUID format")
    }
  }
  implicit val zonedDateTimeFormat = new JsonFormat[ZonedDateTime] {
    override def read(json: JsValue): ZonedDateTime = {
      json match {
        case JsString(zonedDT) => Try(ZonedDateTime.parse(zonedDT, formatter)).getOrElse(deserializationError(deserializationErrorMessage))
        case _ => deserializationError(deserializationErrorMessage)
      }
    }
    override def write(obj: ZonedDateTime): JsValue = JsString(formatter.format(obj))
    private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    private val deserializationErrorMessage = s"Expected zoned date time in ISO offset date time format ex. ${ZonedDateTime.now().format(formatter)}"
  }
}

object DateTimestampConversion {
  implicit def timestampToJson(timestamp: Timestamp): JsValue = JsString(timestamp.toString)
  implicit def jsonToTimestamp(json: JsValue): Timestamp = new Date
  implicit def dateToTimestamp(date: Date): Timestamp = new Timestamp(date.getTime)
  implicit def zonedToTimestamp(date: ZonedDateTime): Timestamp = new Timestamp(date.getNano)
}

object TimestampReader extends RootJsonReader[Timestamp] {
  import DateTimestampConversion._
  def read(json: JsValue): Timestamp = json match {
    case _: JsValue => new java.util.Date
    case _ => throw DeserializationException("Wrong date format.")
  }
}

object TimestampWriter extends RootJsonWriter[Timestamp] {
  def write(timestamp: Timestamp): JsValue = JsString(timestamp.toString)
}