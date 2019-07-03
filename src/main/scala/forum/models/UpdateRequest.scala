package forum.models

case class UpdateRequest(id: Id, secret: Secret, content: Content)
