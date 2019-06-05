package forum

import java.sql.Timestamp


case class Topic(id: Option[Int], nickname: String, mail: String, topic: String, content: String, lastActivity: Timestamp, secret: Int)
case class Answer(id: Option[Int], nickname: String, mail: String, topicID: Int, content: String, lastActivity: Timestamp, secret: Int)

case class TopicInput(nickname: String, mail: String, topic: String, content: String)
case class AnswerInput(nickname: String, mail: String, topicID: Int, content: String)

case class UpdateRequest(id: Int, secret: Int, content: String)
case class DeleteRequest(id: Int, secret: Int)
case class Status(status: String)


case class ErrorMessage(message: String)
case class SuccessMessage(message: String)
