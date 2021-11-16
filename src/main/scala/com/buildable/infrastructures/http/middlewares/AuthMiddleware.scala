package com.buildable.infrastructures.http.middlewares

import scala.util.control.NoStackTrace
import cats.data.{Kleisli, OptionT}
import org.http4s._
import cats.effect.IO
import org.typelevel.ci.CIString

sealed abstract class TwitterAuthError(val message: String) extends NoStackTrace
object TwitterAuthError      {
  case object NoAuthHeader extends TwitterAuthError("Could not find an Authorization header")
  case object Timeout      extends TwitterAuthError("The request time window is closed")
  case object Unauthorized extends TwitterAuthError("Wrong permissions")
}

object TwitterAuthMiddleware {
  private def verifyFromHeader(
      req: Request[IO]
  ): Either[TwitterAuthError, Unit]                 =
    for {
      _ <- req.headers
             .get(CIString("Buildable"))
             .map(_.head.value)
             .toRight(TwitterAuthError.NoAuthHeader)
    } yield ()

  def apply(routes: HttpRoutes[IO]): HttpRoutes[IO] =
    Kleisli { req: Request[IO] =>
      verifyFromHeader(req) match {
        case Left(error) =>
          OptionT.some[IO](Response[IO](Status.Unauthorized).withEntity(error.message))
        case Right(_)    => routes(req)
      }
    }
}
