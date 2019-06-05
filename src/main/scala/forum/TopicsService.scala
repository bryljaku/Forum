package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date
import scala.language.postfixOps


object TopicsService extends DbBase with InputHandler {
    private def topicValidation(id: Int, secret: Int) = topicsTable.filter(t => t.id === id && t.secret === secret)

    def findTopics(page: Option[Int], limit: Option[Int]): Future[List[Topic]] = {
        val limitVal: Int = limit match {
            case Some(lim) if lim <= TOPICSLIMIT => lim
            case _ => TOPICSLIMIT
        }
        val pageVal: Int = page match {
            case Some(pag) => pag
            case _ => 0
        }
        topicsTable.to[List].sortBy(_.lastActivity.desc).drop(pageVal * limitVal).take(limitVal).result
    }
    def findTopic(topicId: Int): Future[Option[Topic]] = topicsTable.filter(_.id === topicId).result.headOption

    def createTopic(topic: TopicInput): Option[Future[Int]] = {
        def createTopicHelper(topic: TopicInput): Future[Int] = (topicsTable returning topicsTable.map(_.secret) += topicFromInput(topic))
        val validateMail: Boolean= topic.mail contains '@'
        val validateContent: Boolean = (topic.content).size > 0
        val validateTopic: Boolean = (topic.topic).size > 0 

        if (validateMail && validateContent && validateTopic) 
            Some(createTopicHelper(topic))
        else
            None
    }

    def updateTopic(request: UpdateRequest): Future[Int] =
    topicValidation(request.id, request.secret)
    .map(t => (t.content, t.lastActivity))
    .update((request.content, new Date))

    def deleteTopic(request: DeleteRequest): Future[Int] = topicValidation(request.id, request.secret).delete // answers cascade on delete 
}

