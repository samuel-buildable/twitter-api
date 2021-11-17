package com.buildable.domains

import com.buildable.infrastructures.ext.http4s.queryParam
import com.buildable.infrastructures.ext.http4s.refined._

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import io.circe.refined._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{Decoder, Encoder, HCursor}
import io.estatico.newtype.macros.newtype

object user {
  @derive(decoder, encoder, show)
  @newtype
  case class UserId(value: String)

  @derive(decoder, encoder, show)
  @newtype
  case class Name(value: NonEmptyString)

  @derive(decoder, encoder, show)
  @newtype
  case class Username(value: String)

  @derive(decoder, encoder, show)
  case class User(id: UserId, name: Name, username: Username)

  @derive(encoder, show)
  case class UserResponse(data: User)

  object UserResponse                         {
    implicit val userResponseDecoder: Decoder[UserResponse] = { (cursor: HCursor) =>
      val dataCursor = cursor.downField("data")
      for {
        data <- dataCursor.as[User]
      } yield UserResponse(data)
    }

    /**
      * TODO: Implement decoder in a different fashion
      *
      * Hint: Encoder.forProduct1("data")
      */
  }

  // Query params, deriving option number two
  @derive(queryParam, show)
  @newtype
  case class UserParam(value: NonEmptyString) {
    def toDomain: Username = Username(value.toLowerCase)

    def toDomainId: UserId = UserId(value.toLowerCase)
  }

  object UserParam                            {
    implicit val jsonDecoder: Decoder[UserParam] =
      Decoder.forProduct1("username")(UserParam.apply)

    implicit val jsonEncoder: Encoder[UserParam] =
      Encoder.forProduct1("username")(_.value)

    // Option #1, this makes the implicit for the param decoder viable
    /**
     implicit val paramDecoder: QueryParamDecoder[UserParam] =
        QueryParamDecoder.stringQueryParamDecoder[UserParam]
      */
  }
}
