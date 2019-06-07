package forum

import java.util.Date
import java.sql.Timestamp
import forum.DateTimestampConversion._
import scala.util.Random

trait InputHandler {
  def getSecret = 1000 + new Random().nextInt(9000)
  def topicFromInput(t: TopicInput) = Topic(None, t.nickname, t.mail,t.topic, t.content, new Date: Timestamp, getSecret)
  def answerFromInput(a: AnswerInput, topicId: Int) = Answer(None, a.nickname, a.mail, topicId, a.content, new Date: Timestamp, getSecret)
}