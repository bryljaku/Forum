package forum

import java.time.ZonedDateTime


case class Topic(id: Option[Int], nickname: String, mail: String, topic: String, content: String, lastActivity: ZonedDateTime, secret: Int)

case class Answer(id: Option[Int], nickname: String, mail: String, topicID: Int, content: String, lastActivity: ZonedDateTime, secret: Int)

case class TopicInput(nickname: String, mail: String, topic: String, content: String)
case class AnswerInput(nickname: String, mail: String, content: String)

case class UpdateRequest(id: Int, secret: Int, content: String)
case class DeleteRequest(id: Int, secret: Int)

case class ErrorMessage(message: String)
case class SuccessMessage(message: String)
case class ContentCreatedMessage(message: String, id: Int, secret: Int)

object ErrorMessage {
    val wrongInput = "Something is wrong with your input."
    val wrongUpdate = "Your update input is invalid. Try again with less content"
    val delete = "Unable to delete. Check your secret and try again"
    val update = "Unable to update. Check your secret and try again"
    val findAnswers = "Couldn't find answers for topic with given ID"
    val findTopic = "Couldn't find topic"
}
object SuccessMessage {
    val update = "Content updated successfully"
    val delete = "Content deleted successfully"
    val create = "Content created successfully"
}