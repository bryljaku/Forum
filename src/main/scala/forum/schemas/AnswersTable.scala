package forum.schemas

import forum.models._
import java.sql.Timestamp
import pl.iterators.kebs._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

class AnswersTable(tag: Tag) extends Table[Answer](tag, "answers") with Kebs {
  def * : ProvenShape[Answer] = (id, nickname, mail, topicID, content, lastActivity, secret) <> ((Answer.apply _).tupled, Answer.unapply)

  def id: Rep[Id] = column[Id]("id", O.PrimaryKey)

  def nickname: Rep[Nickname] = column[Nickname]("nickname")

  def mail: Rep[Mail] = column[Mail]("mail")

  def topicID: Rep[Id] = column[Id]("topic_id")

  def content: Rep[Content] = column[Content]("content")

  def lastActivity: Rep[Timestamp] = column[Timestamp]("last_activity")

  def secret: Rep[Secret] = column[Secret]("secret")

  def topic_fk: ForeignKeyQuery[TopicsTable, Topic] = foreignKey("answers_topics_id_fkey", topicID, TableQuery[TopicsTable])(_.id, onDelete = ForeignKeyAction.Cascade)
}