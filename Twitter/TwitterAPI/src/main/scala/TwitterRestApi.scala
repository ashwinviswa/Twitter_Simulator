package main.scala

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import spray.can.Http.RegisterChunkHandler
import akka.event.Logging
import akka.io.IO
import spray.can.Http
import spray.routing.HttpService
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport
import akka.remote._
import akka.pattern.ask
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
import spray.json.pimpAny
import spray.json.pimpString
import scala.collection.mutable.ArrayBuffer



class TwitterRestApi(server:Array[ActorSelection]) extends Actor with ActorLogging {
  implicit val timeout: Timeout = 20.second 
  import context.dispatcher

  def receive = {
    
    case _: Http.Connected => 
      sender ! Http.Register(self)


      /*
       * Get the tweet list
       */
      
    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getTweetList" =>
     
	      val senderMachine = sender
	      var clientId= path.split("/").last.toInt
	      println("Client Id For retrieving tweets: "+clientId)
	            
	      var future: Future[TweetList] = (server(clientId%64) ? GetTweetList(clientId)).mapTo[TweetList]
	     
	     future.onSuccess
	     {
	        	case result: TweetList =>
	        	println("test.."+result)
	            val body = HttpEntity(ContentTypes.`application/json`, result.toString)
	            senderMachine ! HttpResponse(entity = body)
	     }
	      
    
      /*
       * Send a tweet to the server
       */
      
    case HttpRequest(POST, Uri.Path(path), _, _, _) if path startsWith "/tweet" =>
        
         	val senderMachine = sender	
        	
         	var tweet= path.split("/").last.split("&")
            println("Tweeter ID: "+tweet(0) + " - Tweet : "+tweet(1))

            var future: Future[String] = (server((tweet(0).toInt)%64) ? sendTweet(tweet(0).toInt,tweet(1))).mapTo[String]
     
            future.onSuccess
            {
        	case result: String =>
        	val body = HttpEntity(ContentTypes.`application/json`, result)
            //println("test.."+body)
            senderMachine ! HttpResponse(entity = body)
            }
         	
     
    /*
     * To get the followers
     */ 
    
    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/getFollowers"=>
     
	     val senderMachine = sender
	     var clientId= path.split("/").last.toInt
	     var future: Future[FollowerList] = (server(clientId%64) ? GetFollowersList(clientId)).mapTo[FollowerList]
	     
	     future.onSuccess
	     {
	        	case result: FollowerList =>
	        	println("Result : "+result)
	            val body = HttpEntity(ContentTypes.`application/json`, result.toString)
	            senderMachine ! HttpResponse(entity = body)
	     } 
     
     /*
      * GEt the hash tag count 
      */
	    case HttpRequest(GET, Uri.Path("/getHashtagCount"), _, _, _) =>
     
	     val senderMachine = sender
	     
	     var future: Future[HashTagCount] = (server(1) ? GetHashTagCount()).mapTo[HashTagCount]
	     
	     future.onSuccess
	     {
	        	case result: HashTagCount =>
	        	println("Hash tag count : "+result)
	            val body = HttpEntity(ContentTypes.`application/json`, result.toString)
	            senderMachine ! HttpResponse(entity = body)
	     }  
	     
	     
     /*
      * To register a client to server
      */
	     
     case HttpRequest(POST, Uri.Path(path), _, _, _) if path startsWith "/register" =>
     
	     val senderMachine = sender
	     var clientId= path.split("/").last.toInt
	     println("client Id for registration: "+clientId)
	     
	     var future: Future[String] = (server(clientId%64) ? Register(clientId)).mapTo[String]
	     
	     future.onSuccess
	     {
	        	case result: String =>
	        	println("test.."+result)
	            val body = HttpEntity(ContentTypes.`application/json`, result)
	            senderMachine ! HttpResponse(entity = body)
	     }  
  }

}
sealed trait Twitsim
case class sendTweet(userId : Int, tweet:String) extends Twitsim
case class Register(clientId : Int) extends Twitsim
case class GetTweetList(clientId : Int) extends Twitsim
case class TweetList(tweetList : ArrayBuffer[String]) extends Twitsim
case class GetFollowersList(clientId : Int) extends Twitsim
case class FollowerList(followerList : ArrayBuffer[Integer]) extends Twitsim
case class GetHashTagCount() extends Twitsim
case class HashTagCount(count : Integer) extends Twitsim
