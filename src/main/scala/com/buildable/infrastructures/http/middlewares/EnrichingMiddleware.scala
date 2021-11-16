package com.buildable.infrastructures.http.middlewares

import scala.concurrent.duration.{FiniteDuration, _}

import org.http4s._
import cats.effect._
import org.http4s.server.middleware._

object EnrichingMiddleware {
  val from: HttpRoutes[IO] => HttpRoutes[IO] = {
    { http: HttpRoutes[IO] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[IO] =>
      CORS(http)
    } andThen { http: HttpRoutes[IO] =>
      Timeout(60.seconds)(http)
    } andThen { http: HttpRoutes[IO] =>
      TwitterAuthMiddleware(http)
    }
  }
}
