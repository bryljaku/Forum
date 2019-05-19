package forum

import java.sql.Timestamp

case class Topic(id: Int, nickname: String, topic: String, content: String, secret: Int, lastActivity: Timestamp)
case class Answer(id: Int, nickname: String, topicID: Int, content: String, secret: Int, createdOn: Timestamp)


