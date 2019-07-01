package forum

import java.sql.Timestamp
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pl.iterators.kebs.json.KebsSpray
import spray.json._

import scala.language.implicitConversions

trait Protocols extends SprayJsonSupport with DefaultJsonProtocol with KebsSpray {
    implicit val printer = PrettyPrinter
    implicit val timestampFormat: JsonFormat[Timestamp] = jsonFormat[Timestamp](TimestampReader, TimestampWriter)
    implicit val topicInputFormat: RootJsonFormat[TopicInput] = jsonFormat4(TopicInput)
    implicit val answerInputFormat: RootJsonFormat[AnswerInput] = jsonFormat3(AnswerInput)
    implicit val updateRequestFormat: RootJsonFormat[UpdateRequest] = jsonFormat3(UpdateRequest)
    implicit val deleteRequestFormat: RootJsonFormat[DeleteRequest] = jsonFormat2(DeleteRequest)
    implicit val answerFormat: RootJsonFormat[Answer] = jsonFormat7(Answer)
    implicit val topicFormat: RootJsonFormat[Topic] = jsonFormat7(Topic)
    implicit val errorMessageFormat: RootJsonFormat[ErrorMessage] = jsonFormat1(ErrorMessage.apply)
    implicit val successMessageFormat: RootJsonFormat[SuccessMessage] = jsonFormat1(SuccessMessage.apply)
    implicit val contentCreatedMessage: RootJsonFormat[ContentCreatedMessage] = jsonFormat3(ContentCreatedMessage)

}

object DateTimestampConversion{
  implicit def timestampToJson(timestamp: Timestamp): JsValue = JsString(timestamp.toString)
  implicit def jsonToTimestamp(json: JsValue): Timestamp = new Date
  implicit def dateToTimestamp(date: Date): Timestamp = new Timestamp(date.getTime)
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