package forum
//it will be changed to connect with database, created to test routes
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.server.Directives._

object Database {
    case object GetAllTopics
    case class GetTopic(id: Int)
    case class GetAnswersForTopic(id: Int)
    case class AddTopic(topic: Topic)
    case class AddAnswer(answer: Answer)
    case class DeleteTopic(topic: Topic)
    case class DeleteAnswer(answer: Answer)
    case object OperationSuccess
}

class Database extends Actor with ActorLogging {
    import Database._
    var topics: Map[Int, Topic] = Map()
    var answers: Map[Int, Answer] = Map()

    override def receive: Receive = {
        case GetAllTopics =>
            log.info("getting all topics")
        case GetTopic(id) =>
            log.info(s"getting topic $id")
        case GetAnswersForTopic(id) =>
            log.info(s"getting answers for topic $id")
            sender() ! answers.values.toList.filter(_.topicID == id)
        case AddTopic(topic) =>
            log.info(s"adding topic $topic")
        case AddAnswer(answer) =>
            log.info(s"adding answer $answer")
        case DeleteTopic(topic) =>
            log.info(s"deleting topic $topic")
            topics = topics - topic.id
            // answers = answers.filter(_.topicID != topic.id)
            sender() ! OperationSuccess
        case DeleteAnswer(answer) =>
            log.info(s"deleting answer $answer")
            answers = answers - answer.id
            sender() ! OperationSuccess
    }
}

