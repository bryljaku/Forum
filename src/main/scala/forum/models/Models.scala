package forum.models

import java.util.UUID

case class Id(value: UUID) extends AnyVal
case class Nickname(nickname: String) extends AnyVal
case class TopicName(topicName: String) extends AnyVal
case class Mail(mail: String) extends AnyVal
case class Content(content: String) extends AnyVal
case class Secret(secret: Int) extends AnyVal




