package forum

import java.time.ZonedDateTime

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape


class TopicsTable(tag: Tag) extends Table[Topic](tag, "topics") {
  def * : ProvenShape[Topic] = (id.?, nickname, mail, topic, content, lastActivity, secret) <> ((Topic.apply _).tupled, Topic.unapply)

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def nickname: Rep[String] = column[String]("nickname")

  def mail: Rep[String] = column[String]("mail")

  def topic: Rep[String] = column[String]("topic")

  def content: Rep[String] = column[String]("content")

  def lastActivity: Rep[ZonedDateTime] = column[ZonedDateTime]("lastActivity")

  def secret: Rep[Int] = column[Int]("secret")
}