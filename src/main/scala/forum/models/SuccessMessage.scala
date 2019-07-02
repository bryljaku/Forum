package forum

case class SuccessMessage(message: String) extends AnyVal
case class ContentCreatedMessage(message: String, id: Id, secret: Secret)

object SuccessMessage {
  val update = "Content updated successfully"
  val delete = "Content deleted successfully"
  val create = "Content created successfully"
}