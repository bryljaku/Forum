package forum

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.pattern.ask
import spray.json._
import scala.util.{Failure, Success}
import scala.io.StdIn.readLine 
import scala.concurrent.{ Await, ExecutionContext, Future }



object Forum extends Routes {
    def main(args: Array[String]) {
    implicit val system = ActorSystem("Marshaller")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    val serverBinding =
    Http().bindAndHandle(route, "localhost", 8080)
    
    println(s"Server online at http://localhost:8080/\n")

    readLine() 

    serverBinding
      .flatMap(_.unbind()) 
      .onComplete(_ => system.terminate())
    }
}