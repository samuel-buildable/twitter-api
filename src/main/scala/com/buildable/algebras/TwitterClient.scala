package com.buildable.algebras

import com.buildable.domains.tweet.TweetResponse
import com.buildable.domains.user.{UserId, Username, UserResponse}
import com.buildable.domains.UserError
import com.buildable.domains.TweetError

import cats.effect._
import cats.syntax.all._
import org.http4s.Method._
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import com.buildable.domains.tweet

trait TwitterClient {
  def getUserByUserName(username: Username, token: String): IO[UserResponse]
  def getTweetsByUserId(userId: UserId, token: String): IO[TweetResponse]
}

object TwitterClient {
  def make(
      client: Client[IO]
  ): TwitterClient = {
    new TwitterClient with Http4sClientDsl[IO] {
      val baseUri = "https://api.twitter.com/2/users/"

      override def getUserByUserName(username: Username, token: String): IO[UserResponse] =
        IO.fromEither(Uri.fromString(baseUri + "by/username/" + username.value)).flatMap { uri =>
          client
            .run(
              GET(
                uri,
                Authorization(
                  Credentials.Token(
                    AuthScheme.Bearer,
                    token
                  )
                ),
                Accept(MediaType.application.json)
              )
            )
            .use { resp =>
              resp.status match {
                case Status.Ok =>
                  resp
                    .asJsonDecode[UserResponse]
                    .handleErrorWith(err => IO.raiseError(UserError(err.getMessage)))
                case st        =>
                  IO.raiseError(
                    UserError(s"Unexpected status: ${Option(st.reason).getOrElse("Unknown")}")
                  )
              }
            }
        }

      override def getTweetsByUserId(userId: UserId, token: String): IO[TweetResponse]    = {

        /**
          * TODO: Implement getTweetsByUserId
          *
          * Hint: You can check the implementation of getUserByUserName.
          */
        ???
      }
    }
  }
}
