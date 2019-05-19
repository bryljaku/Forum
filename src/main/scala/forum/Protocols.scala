package forum

import DateConversion._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import java.sql.Timestamp
import scala.language.implicitConversions

trait ForumJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val topicFormat = jsonFormat6(Topic)
    implicit val answerFormat = jsonFormat6(Answer)
}