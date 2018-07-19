package com.wealthmigrate

//akka is our server
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

//using package circe in order json our data
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import org.mongodb.scala.bson.conversions.Bson

import org.mongodb.scala._

import scala.collection.mutable

case class Person (firstname:String, surname:String, age:Int)
case class Investment(investmentName:String, amount:Double)

object main extends App {

  var db = mutable.MutableList[Person]()
  var investmentDB = mutable.MutableList[Investment]()

  implicit val system = ActorSystem("scala-api")
  implicit val materializer = ActorMaterializer() // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  try {
    // Create a codec for the Person case class
    import org.mongodb.scala.bson.codecs.Macros._
    import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
    import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }
    val codecRegistry = fromRegistries(fromProviders(classOf[Investment]), DEFAULT_CODEC_REGISTRY)

    //directly connecting to the default server on localhost port 27017
    val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
    val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
    val collection: MongoCollection[Investment] = database.getCollection("test")
    println("Connected to Mongo.")
  }
  catch{
    case e: Throwable => println(e)
  }


  val healthCheck = pathPrefix("healthcheck"){
    get{
      complete(StatusCodes.OK, "OK")
    }
  }


  val investRoute = pathPrefix("investments"){
    get{
      complete(StatusCodes.OK, investmentDB.toList.asJson.noSpaces)
    }~
    post{
      entity(as[String]){ aInvestment =>
        decode[Investment](aInvestment) match{
          case Left(e) => complete(StatusCodes.BadRequest, e)
          case Right(investment) =>
            investmentDB += investment
            complete(StatusCodes.Created, investment.asJson.noSpaces)
        }
      }
    }
  }
  println("API running on http://localhost:8080")
  Http().bindAndHandle(healthCheck ~ investRoute, "0.0.0.0", 8080)
}
