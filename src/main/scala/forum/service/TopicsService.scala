package forum

import java.time.ZonedDateTime

import forum.ContentAndPaginationValidation._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.language.postfixOps

object TopicsService extends BaseService with InputHandler {
  def findTopics(page: Option[Int], limit: Option[Int]): Future[List[Topic]] = {
    val (pageVal, limitVal): (Int, Int) = validateAndCorrectTopicsPagination(page, limit)
    topicsTable.to[List].sortBy(_.lastActivity.desc).drop(pageVal * limitVal).take(limitVal).result
  }

  def findTopic(topicId: Int): Future[Option[Topic]] = topicsTable.filter(_.id === topicId).result.headOption

  def createTopic(topic: TopicInput): Option[Future[(Int, Int)]] = {
    def createTopicHelper(topic: TopicInput): Future[(Int, Int)] = topicsTable returning topicsTable.map(x => (x.id, x.secret)) += topicFromInput(topic)

    if (validateTopicInput(topic)) Some(createTopicHelper(topic))
    else None
  }

  def updateTopic(request: UpdateRequest): Option[Future[Int]] = {
    def updateAction: Future[Int] = topicCheckSecret(request.id, request.secret)
      .map(t => (t.content, t.lastActivity))
      .update((request.content, ZonedDateTime.now()))

    if (validateUpdateRequest(request)) Some(updateAction)
    else None
  }

  def deleteTopic(request: DeleteRequest): Future[Int] = topicCheckSecret(request.id, request.secret).delete

  private def topicCheckSecret(id: Int, secret: Int) = topicsTable.filter(t => t.id === id && t.secret === secret)
}

