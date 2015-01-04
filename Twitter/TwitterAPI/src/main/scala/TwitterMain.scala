package main.scala

import akka.event.Logging
import akka.io.IO
import spray.can.Http
import spray.routing.HttpService
import akka.actor._
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport
import akka.remote._
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Timeout.durationToTimeout
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.InetAddress
import akka.pattern.pipe
import scala.concurrent.duration.DurationInt
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import spray.http.HttpEntity
import spray.http.HttpEntity.apply
import spray.can.Http
import spray.http.ContentTypes
import spray.http.HttpMethods.GET
import spray.http.HttpMethods.POST
import spray.http.HttpRequest
import spray.http.HttpResponse



object TwitterMain{
  
  def main(args: Array[String]) {
    
    
     	implicit val configSystem = ActorSystem("HTTPServer", ConfigFactory.load(ConfigFactory.parseString("""{ "akka" : { "actor" : { "provider" : "akka.remote.RemoteActorRefProvider" }, "remote" : { "enabled-transports" : [ "akka.remote.netty.tcp" ], "netty" : { "tcp" : { "port" : 5185 , "maximum-frame-size" : 12800000b } } } } } """)))
     	//var server =   = null
     	var server = new Array[ActorSelection](64)
     	for(i <-0 until 64)
     	{
     		server(i) = configSystem.actorSelection("akka.tcp://MasterActor@127.0.0.1:5185/user/Master"+i)
     		
     	}
     	val handler = configSystem.actorOf(Props(new TwitterRestApi(server)), name = "handler")
     	//bind our actor to an HTTP port
     	IO(Http) ! Http.Bind(handler, interface = "127.0.0.1", port = 8006)
  }
}