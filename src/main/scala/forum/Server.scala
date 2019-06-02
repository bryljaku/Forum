package forum

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.io.StdIn.readLine
import slick.driver.PostgresDriver.api._


object Server extends Routes {
  def main(args: Array[String]) {
    implicit val system: ActorSystem = ActorSystem("Forum")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import system.dispatcher

    val interface = ConfigFactory.load().getString("app.interface")
    val port = ConfigFactory.load().getString("app.port").toInt

    val serverBinding = Http().bindAndHandle(route, interface, port)

    println(s"Server online at http://$interface:$port/\n")
    DbStart.startDB
    println(findTopics(0,15).mapTo[List[Topic]])
    
    readLine()
    
    db.close
    serverBinding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}