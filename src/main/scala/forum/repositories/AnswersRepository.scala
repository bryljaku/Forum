package forum.repositories
import java.time.ZonedDateTime
import forum.DateTimestampConversion._
import forum.{Answer, AnswerInput, AnswersTable, DeleteRequest, Id, Secret, UpdateRequest}
import slick.dbio.DBIO
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import pl.iterators.kebs._


class AnswersRepository extends Kebs {
  import pl.iterators.kebs._
  private val answersTable = TableQuery[AnswersTable]

  private def answerCheckSecret(id: Id, secret: Secret) =
    answersTable.filter(a => a.id === id && a.secret == secret)

  def findTopicAnswersAction(topicId: Id, offset: Int, limit: Int): DBIO[Seq[Answer]] =
    answersTable.filter(_.topicID === topicId)
      .sortBy(_.lastActivity.asc)
      .drop(offset)
      .take(limit).result

  def updateAction(request: UpdateRequest): DBIO[Int] =
    answerCheckSecret(request.id, request.secret)
    .map(a => (a.content, a.lastActivity))
    .update((request.content, ZonedDateTime.now()))

  def findAnswerAction(answerId: Id): DBIO[Option[Answer]] =
      answersTable
        .filter(_.id === answerId)
        .result
        .headOption

  def insertAction(answer: Answer): DBIO[(Id, Secret)] =
    answersTable returning answersTable.map(x => (x.id, x.secret)) += answer

  def deleteAnswerAction(request: DeleteRequest): DBIO[Int] =
    answerCheckSecret(request.id, request.secret).delete
}
