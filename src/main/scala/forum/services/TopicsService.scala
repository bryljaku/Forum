package forum.services

import forum.models._
import forum.repositories.TopicsRepository
import forum.services.ContentAndPaginationValidation._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class TopicsService(db: Database, topicsRepository: TopicsRepository) {

  def findTopics(page: Option[Int], limit: Option[Int]): Future[Seq[Topic]] = {
    val (pageVal, limitVal): (Int, Int) = validateAndCorrectTopicsPagination(page, limit)
    val offset = pageVal * limitVal
    db.run(topicsRepository.findTopics(offset, limitVal))
  }
  def findTopic(topicId: Id): Future[Either[ErrorMessage, Topic]] =
    db.run(topicsRepository.findTopic(topicId)).map{
      case Some(t) => Right(t)
      case _ => Left(ErrorMessage(ErrorMessage.findTopic))
    }

  def createTopic(topicInput: TopicInput): Future[Either[ErrorMessage, CreatedResponse]] = {
    validateTopicInput(topicInput) match {
      case true => db.run(topicsRepository.addTopic(Topic.from(topicInput))).map {
        case s:(Id, Secret) => Right(CreatedResponse(s._1, s._2))
        case _ => Left(ErrorMessage(ErrorMessage.databaseError))
      }

      case false => Future.successful(Left(ErrorMessage(ErrorMessage.wrongInput)))
    }
  }
  def updateTopic(request: UpdateRequest): Future[Either[ErrorMessage, SuccessMessage]]= {
        validateUpdateRequest(request) match {
      case true => db.run(topicsRepository.updateTopic(request)).map{
        case 1 => Right(SuccessMessage(SuccessMessage.update))
        case _ => Left(ErrorMessage(ErrorMessage.wrongUpdate))
      }
      case false => Future.successful(Left(ErrorMessage(ErrorMessage.wrongInput)))
    }
  }
  def deleteTopic(request: DeleteRequest): Future[Either[ErrorMessage, SuccessMessage]] =
    db.run(topicsRepository.deleteTopic(request)).map{
      case 1 => Right(SuccessMessage(SuccessMessage.delete))
      case _ => Left(ErrorMessage(ErrorMessage.delete))
    }
}
