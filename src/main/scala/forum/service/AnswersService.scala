package forum

import forum.ContentAndPaginationValidation._
import forum.repositories.AnswersRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.language.postfixOps
class AnswersService(db: Database, answersRepository: AnswersRepository) extends  AnswersRepository {


  def findTopicAnswers(topicId: Id, mid: Int, before: Int, after: Int) = {
    val (beforeVal, afterVal): (Int, Int) = validateAndCorrectAnswersPagination(before, after, mid)
    val offset = mid - beforeVal
    val limit = beforeVal + afterVal + 1

    db.run(answersRepository.findTopicAnswersAction(topicId, offset, limit))
  }

  def findAnswer(answerId: Id): Future[Option[Answer]] =
    db.run(answersRepository.findAnswerAction(answerId))

  def createAnswer(answerInput: AnswerInput, topicId: Id): Option[Future[(Id, Secret)]] = {
    val a = Answer.from(answerInput, topicId)
    if (validateAnswerInput(answerInput)) Some(db.run(answersRepository.insertAction(a)))
    else None
  }

  def updateAnswer(request: UpdateRequest): Option[Future[Int]] = {
    if (validateUpdateRequest(request)) Some(db.run(answersRepository.updateAction(request)))
    else None
  }

  def deleteAnswer(request: DeleteRequest): Future[Int] =
    db.run(answersRepository.deleteAnswerAction(request))
}
