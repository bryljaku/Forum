package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date

trait AnswersService extends DbBase with InputHandler {
 
  private def answerValidation(id: Int, secret: Int) = answersTable.filter(a => a.id === id && a.secret === secret)
  private def updateTopicActivity(topicId: Int) = topicsTable.filter(_.id === topicId).map(_.lastActivity).update(new Date) // 

  def findAnswer(answerId: Int): Future[Option[Answer]] = answersTable.filter(_.id === answerId).result.headOption

  def createAnswer(answer: AnswerInput): Future[Int] = answersTable returning answersTable.map(_.secret) += answerFromInput(answer) // andThen updateTopicActivity

  def updateAnswer(request: UpdateRequest): Future[Int] =
    answerValidation(request.id, request.secret)
    .map(a => (a.content, a.lastActivity))
    .update((request.content, new Date))

  def deleteAnswer(request: DeleteRequest): Future[Int] = answerValidation(request.id, request.secret).delete
}
