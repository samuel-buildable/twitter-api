package com.buildable.infrastructures.http.middlewares

import org.http4s._
import cats.effect._
import org.http4s.server.middleware._

object ClosedMiddleware {
  val from: HttpApp[IO] => HttpApp[IO] = {
    { http: HttpApp[IO] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[IO] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }
}
