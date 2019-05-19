package forum

import scala.language.implicitConversions
import java.sql.Timestamp
import java.util.Date

case class Topic(id: Int, nickname: String, topic: String, content: String, secret: Int, lastActivity: Timestamp)
case class Answer(id: Int, nickname: String, topicID: Int, content: String, secret: Int, createdOn: Timestamp)


object DateConversion {
  implicit def dateToTimestamp(date: Date): Timestamp = new Timestamp(date.getTime)
}