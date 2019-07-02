package forum

import forum.ContentAndPaginationValidation._
import slick.jdbc.PostgresProfile.api._
import forum.repositories.TopicsRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class TopicsService(db: Database, topicsService: TopicsService) extends TopicsRepository {

  def findTopics(page: Option[Int], limit: Option[Int]): Future[List[Topic]] = {
    val (pageVal, limitVal): (Int, Int) = validateAndCorrectTopicsPagination(page, limit)
    val offset = pageVal * limitVal
    db.run(topicsService.findTopicsAction(offset, limitVal))
  }
  def findTopic(topicId: Id): Future[Option[Topic]] =
    db.run(topicsService.findTopicAction(topicId))

  def createTopic(topicInput: TopicInput): Future[Either[ErrorMessage, (Id, Secret)]] = {
    validateTopicInput(topicInput) match {
      case true => db.run(topicsService.createTopicAction(Topic.from(topicInput))).map(Right(_))
      case false => Future.successful(Left(ErrorMessage(ErrorMessage.wrongInput)))
    }

//    if (validateTopicInput(topicInput)) Some(db.run(createTopicAction(Topic(topicInput))))
//    else None
  }

  def updateTopic(request: UpdateRequest): Option[Future[Int]] = {
    validateUpdateRequest(request) match {
      case true => Some(db.run(topicsService.topicUpdateAction(request)))
      case false => None
    }
  }

  def deleteTopic(request: DeleteRequest): Future[Int] =
    db.run(topicsService.deleteTopicAction(request))

}

