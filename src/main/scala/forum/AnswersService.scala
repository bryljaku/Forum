package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date
import scala.language.postfixOps


object AnswersService extends DbBase with InputHandler {
  private def answerValidation(id: Int, secret: Int) = answersTable.filter(a => a.id === id && a.secret === secret)

  def findAnswer(answerId: Int): Future[Option[Answer]] = answersTable.filter(_.id === answerId).result.headOption
  
  
  def createAnswer(answer: AnswerInput): Option[Future[Int]] = {
    def createAnswerHelper(answer: AnswerInput): Future[Int] = answersTable returning answersTable.map(_.secret) += answerFromInput(answer) // andThen updateTopicActivity
    val validateMail = answer.mail contains '@'
    val validateContent = (answer.content.size > 0 && answer.content.size < 200)
    if (validateMail && validateContent)
        Some(createAnswerHelper(answer))
      else None
  }
  def updateAnswer(request: UpdateRequest): Future[Int] =
    answerValidation(request.id, request.secret)
    .map(a => (a.content, a.lastActivity))
    .update((request.content, new Date))

  def deleteAnswer(request: DeleteRequest): Future[Int] = answerValidation(request.id, request.secret).delete
      
  def findTopicAnswers(topicId: Int, mid: Int, before: Int, after: Int) = { 
    def findTopicAnswersHelper(topicId: Int, mid: Int, before: Int, after: Int): Future[List[Answer]] = 
      answersTable.filter(_.topicID === topicId).to[List]
      .sortBy(_.lastActivity.asc)
      .drop(mid - before)
      .take(after).result

    def validatePagination = 
      before + after + 1 <= ANSWERSLIMIT
    def correctPagination = {
      val afterRatio = after / (before + after)
      val beforeRatio = before / (before + after)
      ((beforeRatio * ANSWERSLIMIT) toInt, (afterRatio * ANSWERSLIMIT) toInt)
    }

    if (validatePagination)
      findTopicAnswersHelper(topicId, mid, before, after)
    else {
      val x = correctPagination
      findTopicAnswersHelper(topicId, mid, x._1, x._2)
    }  
  }

}
