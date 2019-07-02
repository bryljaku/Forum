package forum

case class UpdateRequest(id: Id, secret: Secret, content: Content)
