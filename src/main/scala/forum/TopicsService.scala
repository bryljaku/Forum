package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory
import Validation._

object TopicsService extends DbBase with InputHandler {
    private def topicCheckSecret(id: Int, secret: Int) = topicsTable.filter(t => t.id === id && t.secret === secret)

    def findTopics(page: Option[Int], limit: Option[Int]): Future[List[Topic]] = {
        val (pageVal, limitVal): (Int, Int) = validateTopicsPagination(page, limit)
        topicsTable.to[List].sortBy(_.lastActivity.desc).drop(pageVal * limitVal).take(limitVal).result
    }
    def findTopic(topicId: Int): Future[Option[Topic]] = topicsTable.filter(_.id === topicId).result.headOption

    def createTopic(topic: TopicInput): Option[Future[(Int, Int)]] = {
        def createTopicHelper(topic: TopicInput): Future[(Int, Int)] = (topicsTable returning topicsTable.map(x => (x.id, x.secret)) += topicFromInput(topic))

        if (validateTopicInput(topic)) Some(createTopicHelper(topic))
        else None
    }

    def updateTopic(request: UpdateRequest): Option[Future[Int]] = {
        def updateAction: Future[Int] = topicCheckSecret(request.id, request.secret)
        .map(t => (t.content, t.lastActivity))
        .update((request.content, new Date))

        if (validateUpdateRequest(request)) Some(updateAction)
        else None
}
    def deleteTopic(request: DeleteRequest): Future[Int] = topicCheckSecret(request.id, request.secret).delete
}

