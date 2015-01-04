package main.scala



// Akka Actor system
import akka.actor._
import spray.http.{ HttpRequest, HttpResponse }
import spray.client.pipelining.{ Get, sendReceive }
import spray.client.pipelining.{ Post, sendReceive }
import spray.http._
import spray.json.DefaultJsonProtocol
import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
//import scala.actors.Actor
//import scala.actors.ActorRef
import akka.actor.Props
import scala.util.Random

package main.scala {
 
  // trait with single function to make a GET request
  trait WebClient {
    def get(url: String): Future[String]
  }




  // implementation of WebClient trait
  class SprayWebClient(implicit system: ActorSystem) extends WebClient {
   
    // create a function from HttpRequest to a Future of HttpResponse
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
 
    // create a function to send a GET request and receive a string response
    def get(url: String): Future[String] = {
      val futureResponse = pipeline(Get(url))
      futureResponse.map(_.entity.asString)
    }
  }

  object Client extends Application {
    
 
 // bring the actor system in scope
 implicit val system = ActorSystem("Node")
 import system.dispatcher
 // create the client
val webClient = new SprayWebClient()(system)
var clientId = 21
//var msg="this-is-a-tweet"
var tweet="Lorem-ipsum-dolor-sit-amet--consectetur-adipiscing-elit--sed-do-eiusmod-tempor-incididunt-ut-labore-et-dolore-magna-aliquaa-Ut-enim-ad-minim-veniam--quis-nostrud-exercitation-ullamco-laboris-nisi-ut-aliquip-ex-ea-commodo-consequata-Duis-aute-irure-dolor-in-reprehenderit-in-voluptate-velit-esse-cillum-dolore-eu-fugiat-nulla-pariatura-Excepteur-sint-occaecat-cupidatat-non-proident--sunt-in-culpa-qui-officia-deserunt-mollit-anim-id-est-laboruma" 
var parameter:Int=0
var num_of_users=10000
var num_of_users1 = 50
var num_of_users2 = 50
var num_of_users3 = 70
var num_of_users4 = 130
var num_of_users5 = 140
var num_of_users6 = 800
var num_of_users7 = 7500
var range1 = num_of_users1
var range2 = range1 + num_of_users2
var range3 = range2 + num_of_users3
var range4 = range3 + num_of_users4
var range5 = range4 + num_of_users5
var range6 = range5 + num_of_users6
var range7 = range6 + num_of_users7

 def returnparameter(i:Int):Integer = {
        
        if(i>=0 && i<range1)
          parameter=1
        else if(i>=range1 && i<range2)
          parameter=2
        else if(i>=range2 && i<range3)
          parameter=3
        else if(i>=range3 && i<range4)
          parameter=4
        else if(i>=range4 && i<range5)
          parameter=5
        else if(i>=range5 && i<range6)
          parameter=6
        else if(i>=range6 && i<range7)
          parameter=7
          
        return parameter
}
 def sendreq(i:Int,parameter:Int) = {
   var msg=" "
   if(parameter==10)
     msg="tweeting-hash-superbowl"
   else
   {
      var rand=Random.nextInt(250)
      msg=tweet.substring(rand,rand+10) 
  }
     val pipeline: HttpRequest => Future[String] = (
     sendReceive
  ~> unmarshal[String]
)
   //val pipeline: HttpRequest
    // send GET request with absolute URI
   // val futureResponse = webClient.get("http://127.0.0.1:8006/tweet/"+i+"&"+msg)
    //val chk: Future[String] =pipeline(Post("http://127.0.0.1:8006/tweet/"+i+"&"+msg))
  val chk: Future[String]=pipeline(Post("http://127.0.0.1:8006/tweet/"+i+"&"+msg))
    
    // wait for Future to complete
    chk onComplete {
      case Success(response) => //println(response)
      case Failure(error) =>// println("An error has occured: " + error.getMessage)
    }

 }
 
 //register followers functionality
 for(i <- 0 until num_of_users)
  {
   val pipeline: HttpRequest => Future[String] = (
     sendReceive
  ~> unmarshal[String]
) 
val chk: Future[String] =pipeline(Post("http://127.0.0.1:8006/register/"+i))
      chk onComplete {
      case Success(response) => println(response)
      //case Failure(error) => println("An error has occured: " + error.getMessage)
    }
  }
    
//get followers functionality 
 val followers = webClient.get("http://127.0.0.1:8006/getFollowers/10")
 // wait for Future to complete
    followers onComplete {
      case Success(response) => println("Followers of client 10 "+response.toString())
      //case Failure(error) => println("An error has occured: " + error.getMessage)
    }
     //send tweets
     
      //system.scheduler.scheduleOnce(80 seconds){
      for(i <- 0 until num_of_users){
      sendreq(i,10)
      }
     // }
     
      
//send tweets functionality
      for(i <- 0 until num_of_users){      
     parameter = returnparameter(i) 
     parameter match{
       
         case 1 => {
           system.scheduler.schedule(0 seconds,0.008 seconds)(sendreq(i,parameter))
         }
         case 2 => {
           system.scheduler.schedule(0 seconds,.0325 seconds)(sendreq(i,parameter))
         }
         case 3 =>{
           system.scheduler.schedule(0 seconds,0.125 seconds)(sendreq(i,parameter))
         }
         case 4 => {
           system.scheduler.schedule(0 seconds,0.45 seconds)(sendreq(i,parameter))
         }
         case 5 => {
           system.scheduler.schedule(0 seconds,0.970 seconds)(sendreq(i,parameter))
         }
         case 7 => {
           system.scheduler.schedule(0 seconds,0.0010 seconds)(sendreq(i,parameter))
         }
         case 10 => {
           var message = "tweet-message-hash-superbowl"
           
           system.scheduler.schedule(0 seconds, 0.10 seconds)(sendreq(i,parameter))
           
         }
         case _ => {}
        } 
        } 
     
     
    // getTweetList functionality
    val futureResponse = webClient.get("http://127.0.0.1:8006/getTweetList/10")
    // wait for Future to complete
    futureResponse onComplete {
      case Success(response) => println("Tweet list of user 10"+response)
      //case Failure(error) => println("An error has occured: " + error.getMessage)
    }
    
    //gethashtag functionality
    val hashResponse = webClient.get("http://127.0.0.1:8006/getHashtagCount")
    // wait for Future to complete
    hashResponse onComplete {
      case Success(response) => println("Hash tag count " +response)
      //case Failure(error) => println("An error has occured: " + error.getMessage)
    }
    
  }
}