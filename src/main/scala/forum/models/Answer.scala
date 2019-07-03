package forum.models

import java.sql.Timestamp
import java.time.ZonedDateTime
import DateTimestampConversion._
import Generator._


case class Answer(id: Id, nickname: Nickname, mail: Mail, topicID: Id, content: Content, lastActivity: Timestamp, secret: Secret)
case class AnswerInput(nickname: Nickname, mail: Mail, content: Content)

object Answer {
  def from(a: AnswerInput, topicId: Id) = new Answer(getId, a.nickname, a.mail, topicId, a.content, ZonedDateTime.now(), getSecret)
}