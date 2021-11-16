package com.buildable.domains

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.refined._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{Decoder, HCursor}
import io.estatico.newtype.macros.newtype
import io.circe.ACursor
import io.circe.DecodingFailure

object tweet {
  @derive(decoder, encoder, show)
  @newtype
  case class TweetId(value: NonEmptyString)

  @derive(decoder, encoder, show)
  @newtype
  case class TweetText(value: NonEmptyString)

  @derive(decoder, encoder, show)
  case class Tweet(id: TweetId, text: TweetText)

  @derive(decoder, encoder, show)
  case class TweetError(value: TweetId, detail: String, title: String)

  @derive(encoder, show)
  case class TweetResponse(data: List[Tweet])

  // Errors
  @derive(encoder, show)
  case class TweetNotFound(errors: List[TweetError])

  object TweetResponse {

    implicit val tweetResponseDecoder: Decoder[TweetResponse] = { (cursor: ACursor) =>
      val dataCursor = cursor.downField("data")

      for {
        data <- dataCursor.as[List[Tweet]]
      } yield TweetResponse(data)

    }

    implicit val tweetErrorDecoder: Decoder[TweetNotFound]    = {
      Decoder.forProduct1("errors")(TweetNotFound.apply)
    }
  }
}
