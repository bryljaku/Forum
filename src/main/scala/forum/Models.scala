package forum

import java.util.Date
import java.sql.Timestamp
import forum.DateTimestampConversion._
import scala.util.Random


  case class Topic(id: Option[Int], nickname: String, topic: String, content: String, lastActivity: Timestamp, secret: Int)
  case class Answer(id: Option[Int], nickname: String, topicID: Int, content: String, lastActivity: Timestamp, secret: Int)

  case class TopicInput(nickname: String, topic: String, content: String)
  case class AnswerInput(nickname: String, topicID: Int, content: String)

  case class UpdateRequest(id: Int, secret: Int, content: String)
  case class DeleteRequest(id: Int, secret: Int)

trait InputHandler {
  def getSecret = 1000 + new Random().nextInt(9000)
  def topicFromInput(t: TopicInput) = Topic(None, t.nickname, t.topic, t.content, new Date: Timestamp, getSecret)
  def answerFromInput(a: AnswerInput) = Answer(None, a.nickname, a.topicID, a.content, new Date: Timestamp, getSecret)
}
