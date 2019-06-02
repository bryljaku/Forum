package forum

import java.sql.Timestamp
import slick.jdbc.PostgresProfile.api._

trait DbScheme extends Protocols {
class TopicsTable(tag: Tag) extends Table[Topic](tag, "topic") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def nickname = column[String]("nickname")
    def topic = column[String]("topic")
    def content = column[String]("content")
    def lastActivity = column[Timestamp]("lastActivity")
    def secret = column[Int]("secret")

    def * = (id.?, nickname, topic, content, lastActivity, secret) <> ((Topic.apply _).tupled, Topic.unapply)

}
class AnswersTable(tag: Tag) extends Table[Answer](tag, "answers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def nickname = column[String]("nickname")
    def topicID = column[Int]("topicID")
    def content = column[String]("content")
    def lastActivity = column[Timestamp]("lastActivity")
    def secret = column[Int]("secret")

    def * = (id.?, nickname, topicID, content, lastActivity, secret) <> ((Answer.apply _).tupled, Answer.unapply)

    def topic = foreignKey("topic_fk", topicID, TableQuery[TopicsTable])(_.id) 
    }
}