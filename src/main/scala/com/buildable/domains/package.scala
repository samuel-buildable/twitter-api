package com.buildable

import scala.util.control.NoStackTrace

package object domains {
  sealed trait UserOrTweetError extends NoStackTrace {
    def cause: String
  }

  case class UserError(cause: String)  extends UserOrTweetError
  case class TweetError(cause: String) extends UserOrTweetError
}
