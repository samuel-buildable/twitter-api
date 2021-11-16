package com.buildable.infrastructures.http

import com.buildable.algebras.TwitterClient
import com.buildable.domains.user.UserParam
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import cats.effect._
import org.http4s._
import org.http4s.headers.Authorization
import org.typelevel.ci.CIString

object TwitterRoutes {
  def make(
      twitterClient: TwitterClient
  ): HttpRoutes[IO] = {
    new TwitterRoutes(twitterClient).routes
  }
}

final case class TwitterRoutes(twitterClient: TwitterClient) extends Http4sDsl[IO] {

  private[http] val prefixPath = "/users"

  object UserQueryParam extends QueryParamDecoderMatcher[UserParam]("username")

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ GET -> Root :? UserQueryParam(username)          =>
      val token = req.headers
        .get(CIString("Authorization"))
        .map(_.head.value)

      token match {
        case Some(value) =>
          Ok(
            twitterClient
              .getUserByUserName(
                username.toDomain,
                value
              )
          ).handleErrorWith(_ =>
            InternalServerError(
              "Something went wrong while trying to get the user"
            )
          )
        case None        => Forbidden()
      }

    case req @ GET -> Root / "tweets" :? UserQueryParam(userid) =>
      val token = req.headers
        .get(CIString("Authorization"))
        .map(_.head.value)
      token match {
        case Some(value) =>
          Ok(
            twitterClient
              .getTweetsByUserId(
                userid.toDomainId,
                value
              )
          ).handleErrorWith(_ =>
            InternalServerError(
              "Something went wrong while trying to get the tweets"
            )
          )
        case None        => Forbidden()
      }
  }

  val routes: HttpRoutes[IO]             = Router(
    prefixPath -> httpRoutes
  )

}