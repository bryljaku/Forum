package forum.models

import java.sql.Timestamp
import java.time.ZonedDateTime
import forum.models.Generator._
import DateTimestampConversion._

case class Topic(id: Id, nickname: Nickname, mail: Mail, topic: TopicName, content: Content, lastActivity: Timestamp, secret: Secret)
case class TopicInput(nickname: Nickname, mail: Mail, topic: TopicName, content: Content)

object Topic {
  def from(t: TopicInput) = new Topic(getId, t.nickname, t.mail, t.topic, t.content, ZonedDateTime.now(), getSecret)
}
