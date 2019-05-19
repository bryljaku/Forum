package forum

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.io.StdIn.readLine 
import com.typesafe.config.ConfigFactory


object Forum extends Routes {
    def main(args: Array[String]) {
    implicit val system = ActorSystem("ForumSystem")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    val interface = ConfigFactory.load().getString("app.interface")
    val port = ConfigFactory.load().getString("app.port").toInt
    val serverBinding =
    Http().bindAndHandle(route, interface, port)
    
    println(s"Server online at http://$interface:$port/\n")

    readLine() 

    serverBinding
      .flatMap(_.unbind()) 
      .onComplete(_ => system.terminate())
    }
}