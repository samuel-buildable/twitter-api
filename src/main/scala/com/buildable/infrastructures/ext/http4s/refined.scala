package com.buildable.infrastructures.ext.http4s

import cats.syntax.all._
import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, Validate}
import org.http4s._

object refined {

  /**
    * QueryParamDecoder instance for refinement types.
    */
  implicit def refinedQueryParamDecoder[T: QueryParamDecoder, P](implicit
      ev: Validate[T, P]
  ): QueryParamDecoder[Refined[T, P]] =
    QueryParamDecoder[T].emap(refineV[P](_).leftMap(m => ParseFailure(m, m)))
}
