package main.scala
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import scala.collection.mutable.ArrayBuffer
import scala.collection._
import scala.collection.convert.decorateAsScala._
import java.util.concurrent.ConcurrentHashMap
import scala.collection.immutable.TreeMap
import scala.util.Random
import scala.collection.mutable.HashMap
import java.util.concurrent.atomic.AtomicReference

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger



object Project4 extends App
{
var num_of_servernodes:Int=64
var num_of_clientnodes:Int=8
var no_of_clients=10000
var no_of_clientsystems:Int=1
var serverNode= new ArrayBuffer[ActorRef]
var followersMap=new ConcurrentHashMap[Integer,ArrayBuffer[Integer]]
// all client actors present on each system
var actorsMap =new ConcurrentHashMap[Integer,ArrayBuffer[ActorRef]]
//timeline = selfTweetsMap + followingTweetsMap for a particular clientId
var followers_created=new AtomicBoolean(false)
var selfTweetsMap=new ConcurrentHashMap[Integer,ArrayBuffer[String]]
var followingTweetsMap=new ConcurrentHashMap[Integer,ArrayBuffer[String]]
var hashEventMap=new ConcurrentHashMap[String,Integer]
var startTime=System.currentTimeMillis

var num_of_followers1 =950
var num_of_followers2 = 550
var num_of_followers3 = 350
var num_of_followers4 = 200
var num_of_followers5 = 100
var num_of_followers6 = 30
var num_of_followers7 = 10



/*var num_of_followers1 =4750
var num_of_followers2 = 2750
var num_of_followers3 = 1750
var num_of_followers4 = 1000
var num_of_followers5 = 500
var num_of_followers6 = 150
var num_of_followers7 = 50
*/
var num_of_users1 = 50
var num_of_users2 = 50
var num_of_users3 = 70
var num_of_users4 = 130
var num_of_users5 = 140
var num_of_users6 = 800
var num_of_users7 = 7500
/*
var num_of_users1 = 25
var num_of_users2 = 25
var num_of_users3 = 35
var num_of_users4 = 65
var num_of_users5 = 700
var num_of_users6 = 400
var num_of_users7 = 3750
*/
var no_of_tweets_sent=0

var range1 = num_of_users1
var range2 = range1 + num_of_users2
var range3 = range2 + num_of_users3
var range4 = range3 + num_of_users4
var range5 = range4 + num_of_users5
var range6 = range5 + num_of_users6
var range7 = range6 + num_of_users7
val system = ActorSystem("MasterActor")
var i:Int = 0
while(i<num_of_servernodes){
        var a:ActorRef=system.actorOf(Props(new MasterActor(i)),name="Master"+i)
        serverNode+=a
        i=i+1
      }
hashEventMap.put("superbowl", 0)
 class MasterActor(masterId:Int) extends Actor
 {
       def returnparameter(i:Int):Integer = {
      var parameter=0
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
   def genFollowersList() = {

       var permachine=no_of_clients/no_of_clientsystems

       for( clientId <- 0 until no_of_clients )
       {
       var mod=clientId%permachine
       var parameter:Int=returnparameter(mod)
       parameter match{
         case 7 => {
           var num_of_followers=Random.nextInt(num_of_followers7)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
         case 6 => {
           var num_of_followers=num_of_followers7+Random.nextInt(num_of_followers6-num_of_followers7)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
         case 5 =>{
           var num_of_followers=num_of_followers6+Random.nextInt(num_of_followers5-num_of_followers6)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
         case 4 => {
           var num_of_followers=num_of_followers5+Random.nextInt(num_of_followers4-num_of_followers5)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
         case 3 => {
           var num_of_followers=num_of_followers4+Random.nextInt(num_of_followers3-num_of_followers4)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
         case 2 =>{
           var num_of_followers=num_of_followers3+Random.nextInt(num_of_followers2-num_of_followers3)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
         case 1 => {
           var num_of_followers=num_of_followers2+Random.nextInt(num_of_followers1-num_of_followers2)
           followersMap.put(clientId,genFollowers(num_of_followers,clientId))
         }
        }
       }
   }
   def genFollowers(num_of_followers:Int,clientId:Int):ArrayBuffer[Integer]= {
           var followers:ArrayBuffer[Integer]=new ArrayBuffer[Integer]
           var i=0
           while(i<num_of_followers)
           {
             var rand=Random.nextInt(selfTweetsMap.size)
             if(followers.length>0)
             {
              /* 1. generated random client not in followers list already. duplicate check.
               * 2. Is the random generated client registered already
               * 3. The random generated follwer and self are not the same
               */
             if(!followers.contains(rand)&& (selfTweetsMap.get(rand)!=null) && clientId!=rand)
              followers+=rand
             }
             else if(followers.length==0 && (selfTweetsMap.get(rand)!=null) && clientId!=rand)
               followers+=rand
             i+=1;
           }
           return followers
   }
  def receive = {


    case Register(clientId:Int) =>
       selfTweetsMap.put(clientId,ArrayBuffer("registered"))
       println(clientId +" "+ selfTweetsMap.size)
       sender ! "Client "+clientId+" registered!"
       if(selfTweetsMap.size==no_of_clients && !followers_created.get())
       {
         startTime=System.currentTimeMillis
         println("Registered .. Followers creation begins ")
         followers_created.set(true)
         genFollowersList()
         println("selfTweetsMap size "+selfTweetsMap.size)
         println("Followers list created. Size is "+followersMap.size)
         println("Time taken for registration"+ (System.currentTimeMillis-startTime) )
         startTime=System.currentTimeMillis
       }

    case  GetFollowersList(id)=>
      {
        sender ! FollowerList(followersMap.get(id))
      }
    case sendTweet(id,tweet) =>
      {
      if(System.currentTimeMillis-startTime < 180000){

      if(tweet.contains("hash"))
      {
        var temp:Integer=hashEventMap.get("superbowl")+1
        hashEventMap.put("superbowl",temp)
      }
      println("Sender "+id+" sent tweet : "+tweet)

        if(selfTweetsMap.get(id)!=null)
        {
         selfTweetsMap.get(id)+=tweet
         var followers:ArrayBuffer[Integer]=new ArrayBuffer[Integer]
         if(followersMap .get(id)!=null)
          followers=followersMap.get(id)
          var i:Int=0
          no_of_tweets_sent+=1
          while(i<followers.length)
          {
          if(followingTweetsMap.get(followers(i))!=null)
              followingTweetsMap.get(followers(i))+="sent by : "+id+" tweet : "+tweet
              i+=1
          }
        }
      }
      else
       {
         var i=0
         var no_of_tweets=0
         while(i<no_of_clients)
         {
           if (selfTweetsMap.get(i)!=null)
           {
             no_of_tweets+=selfTweetsMap.get(i).length*2
             //println("actor "+i+" no of tweets"+map.apply(i).length+" total clientslength"+clientList.length)
           }
           i+=1
         }
         println("Tweets received "+no_of_tweets+" "+selfTweetsMap.size )
         println("Hashbowl tweets received "+hashEventMap.get("superbowl"))
         context.system.shutdown()
       }
      sender ! "success"
      }
    case GetHashTagCount() =>
      {
        sender ! HashTagCount(hashEventMap.get("superbowl"))
      }
    case GetTweetList(id) =>
      {
        if(followingTweetsMap.get(id)!=null)
         sender ! TweetList(selfTweetsMap.get(id)++followingTweetsMap.get(id))
        else
          sender ! TweetList(selfTweetsMap.get(id))
        
      }
  }
  }
}


sealed trait Twitsim
case class sendTweet(userId:Int,tweet:String) extends Twitsim
case class TweetList(tweetList:ArrayBuffer[String]) extends Twitsim
case class FollowerList(followerList:ArrayBuffer[Integer]) extends Twitsim
case class Register(clientId:Int) extends Twitsim
case class CreateWorker() extends Twitsim
case class sendtoFollowers(clientId:Int,value1:String)extends Twitsim
case class receiveTweet(value1:String,clientId:Int) extends Twitsim
case class GetTweetList(clientId:Int) extends Twitsim
case class GetFollowersList(clientId:Int) extends Twitsim
case class GetHashTagCount()extends Twitsim
case class HashTagCount(count:Integer) extends Twitsim