package com.buildable.domains

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.refined._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.estatico.newtype.macros.newtype
import io.circe.ACursor

object tweet {
  @derive(
    decoder,
    encoder,
    show,
    eqv,
    order
  )        // Automatic derivation for json decoding, json encoding, show (toString), equality, order
  @newtype // zero-cost wrappers with no runtime overhead
  case class TweetId(value: NonEmptyString)

  /**
    * Without the @derive annotation, this would be the equivalent for newtypes.
    * case class Person(age: Person.Age, name: Person.Name)
    *
    *
    * object Person {
    *
    * @newtype case class Age(value: Int)
    *          object Age {
    *          implicit val eq: Eq[Age]       = deriving
    *          implicit val order: Order[Age] = deriving
    *          implicit val show: Show[Age]   = deriving
    *          }
    * @newtype case class Name(value: String)
    *          object Name {
    *          implicit val eq: Eq[Name]       = deriving
    *          implicit val order: Order[Name] = deriving
    *          implicit val show: Show[Name]   = deriving
    *          }
    *
    *          implicit val eq: Eq[Person] = Eq.and(
    *          Eq.by(_.age), Eq.by(_.name)
    *          )
    *
    *          implicit val order: Order[Person] = Order.by(_.name)
    *          implicit val show: Show[Person] =
    *          Show[String].contramap[Person] { p  =>
    *          s"Name: ${p.name.show}, Age: ${p.age.show}"
    *          }
    *          }
    */

  @derive(decoder, encoder, show)
  @newtype
  case class TweetText(value: NonEmptyString)

  @derive(decoder, encoder, show)
  case class Tweet(id: TweetId, text: TweetText)

  @derive(encoder, show)
  case class TweetResponse(data: List[Tweet])

  object TweetResponse {

    implicit val tweetResponseDecoder: Decoder[TweetResponse] = { (cursor: ACursor) =>
      val dataCursor = cursor.downField("data")

      for {
        data <- dataCursor.as[List[Tweet]]
      } yield TweetResponse(data)
    }
  }
}
