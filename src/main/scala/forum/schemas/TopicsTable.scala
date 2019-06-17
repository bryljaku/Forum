package forum

import java.sql.Timestamp
import slick.jdbc.PostgresProfile.api._

class TopicsTable(tag: Tag) extends Table[Topic](tag, "topics") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def nickname = column[String]("nickname")
    def mail = column[String]("mail")
    def topic = column[String]("topic")
    def content = column[String]("content")
    def lastActivity = column[Timestamp]("lastActivity")
    def secret = column[Int]("secret")

    def * = (id.?, nickname, mail, topic, content, lastActivity, secret) <> ((Topic.apply _).tupled, Topic.unapply)
}