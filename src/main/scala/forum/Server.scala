package forum

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.io.StdIn.readLine
import slick.driver.PostgresDriver.api._
import slick.jdbc.H2Profile.api.Database


object Server extends Routes with App {
    val db: Database = Database.forConfig("postgres")

    implicit val system: ActorSystem = ActorSystem("Forum")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import system.dispatcher
    val interface = ConfigFactory.load().getString("app.interface")
    val port = ConfigFactory.load().getString("app.port").toInt
    
    InitializeService.startDB
    val serverBinding = Http().bindAndHandle(route, interface, port)

    println(s"Server online at http://$interface:$port/\n")    
    readLine()
    db.close()
    serverBinding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}