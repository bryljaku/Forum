package forum


import java.time.ZonedDateTime

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

class AnswersTable(tag: Tag) extends Table[Answer](tag, "answers") {
  def * : ProvenShape[Answer] = (id.?, nickname, mail, topicID, content, lastActivity, secret) <> ((Answer.apply _).tupled, Answer.unapply)

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def nickname: Rep[String] = column[String]("nickname")

  def mail: Rep[String] = column[String]("mail")

  def content: Rep[String] = column[String]("content")

  def lastActivity: Rep[ZonedDateTime] = column[ZonedDateTime]("lastActivity")

  def secret: Rep[Int] = column[Int]("secret")

  def topic: ForeignKeyQuery[TopicsTable, Topic] = foreignKey("topic_fk", topicID, TableQuery[TopicsTable])(_.id, onDelete = ForeignKeyAction.Cascade)

  def topicID: Rep[Int] = column[Int]("topicID")
}