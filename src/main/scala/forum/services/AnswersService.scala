package forum.services

import forum.models._
import forum.repositories.AnswersRepository
import forum.services.ContentAndPaginationValidation._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
class AnswersService(db: Database, answersRepository: AnswersRepository) {


  def findTopicAnswers(topicId: Id, mid: Int, before: Int, after: Int): Future[Either[ErrorMessage, Seq[Answer]]] = {
    val (beforeVal, afterVal): (Int, Int) = validateAndCorrectAnswersPagination(before, after, mid)
    val offset = mid - beforeVal
    val limit = beforeVal + afterVal + 1

    db.run(answersRepository.findTopicAnswers(topicId, offset, limit)).map {
      case a: Seq[Answer] if a.nonEmpty => Right(a)
      case _ => Left(ErrorMessage(ErrorMessage.findAnswers))
    }
  }

  def createAnswer(answerInput: AnswerInput, topicId: Id): Future[Either[ErrorMessage,CreatedResponse]] = {
    validateAnswerInput(answerInput) match {
      case true => db.run(answersRepository.addAnswer(Answer.from(answerInput, topicId)))
        .map(x => Right(CreatedResponse(x._1, x._2)))
      case false => Future.successful(Left(ErrorMessage(ErrorMessage.wrongInput)))
    }
  }

  def updateAnswer(request: UpdateRequest): Future[Either[ErrorMessage, SuccessMessage]] = {
    validateUpdateRequest(request) match {
      case true => db.run(answersRepository.updateAnswer(request)).map{
        case 1 => Right(SuccessMessage(SuccessMessage.update))
        case _ => Left(ErrorMessage(ErrorMessage.update))
      }
      case false => Future.successful(Left(ErrorMessage(ErrorMessage.wrongUpdate)))
    }
  }

  def deleteAnswer(request: DeleteRequest): Future[Either[ErrorMessage, SuccessMessage]] =
    db.run(answersRepository.deleteAnswer(request)).map{
      case 1 => Right(SuccessMessage(SuccessMessage.delete))
      case _ => Left(ErrorMessage(ErrorMessage.delete))
    }

}
