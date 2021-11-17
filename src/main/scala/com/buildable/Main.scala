package com.buildable

import scala.concurrent.ExecutionContext.global

import cats.effect._
import scala.concurrent.duration._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.HttpApp
import org.http4s.blaze.client.BlazeClientBuilder
import com.buildable.algebras.TwitterClient
import com.buildable.infrastructures.http.TwitterRoutes
import com.buildable.infrastructures.http.middlewares._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val program: Resource[IO, HttpApp[IO]] = for {
      client       <- BlazeClientBuilder[IO](global).withRequestTimeout(60.seconds).resource
      twitterClient = TwitterClient.make(client)
      routes        = TwitterRoutes.make(twitterClient)
    } yield ClosedMiddleware.from(EnrichingMiddleware.from(routes).orNotFound)

    program.use(app =>
      BlazeServerBuilder[IO](global)
        .withHttpApp(app)
        .bindHttp(8080, "localhost")
        .serve
        .compile
        .lastOrError
    )
  }
}
