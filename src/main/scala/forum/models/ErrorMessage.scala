package forum

case class ErrorMessage(message: String) extends AnyVal

object ErrorMessage {
  val wrongInput = "Something is wrong with your input."
  val wrongUpdate = "Your update input is invalid. Try again with less content"
  val delete = "Unable to delete. Check your secret and try again"
  val update = "Unable to update. Check your secret and try again"
  val findAnswers = "Couldn't find answers for topic with given Id"
  val findTopic = "Couldn't find topic"
}