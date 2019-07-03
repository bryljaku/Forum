package forum.repositories

import java.time.ZonedDateTime

import forum.models.{Protocols, _}
import forum.schemas.AnswersTable
import slick.dbio.DBIO
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import pl.iterators.kebs._
import forum.models.DateTimestampConversion._

class AnswersRepository {
  private val answersTable = TableQuery[AnswersTable]

  private def answerCheckSecret(id: Id, secret: Secret) =
    answersTable.filter(a => a.id === id && a.secret == secret)

  def findTopicAnswers(topicId: Id, offset: Int, limit: Int): DBIO[Seq[Answer]] =
    answersTable.filter(_.topicID === topicId)
      .sortBy(_.lastActivity.asc)
      .drop(offset)
      .take(limit)
      .result

  def addAnswer(answer: Answer): DBIO[(Id, Secret)] =
    answersTable returning answersTable.map(x => (x.id, x.secret)) += answer

  def updateAnswer(request: UpdateRequest): DBIO[Int] =
  answerCheckSecret(request.id, request.secret)
      .map(a => (a.content, a.lastActivity))
      .update((request.content, ZonedDateTime.now()))

  def deleteAnswer(request: DeleteRequest): DBIO[Int] =
    answerCheckSecret(request.id, request.secret).delete
}
