package com.dwolla.fs2aws

import cats.effect.Effect
import cats.implicits._
import com.dwolla.fs2utils.Pagination
import fs2._

import scala.language.{higherKinds, reflectiveCalls}

class PaginatedAwsClient[F[_] : Effect, Req <: PaginatedRequest, Res <: PaginatedResult, T](requestFactory: () ⇒ Req) {
  def via(awsAsyncFunction: AwsAsyncFunction[Req, Res])(extractor: Res ⇒ Seq[T]): Stream[F, T] = {
    val fetchPage = (maybeNextToken: Option[String]) ⇒ {
      val req = requestFactory()
      maybeNextToken.foreach(req.setNextToken)

      req.executeVia[F](awsAsyncFunction).map((res: Res) ⇒ (Segment.seq(extractor(res)), Option(res.getNextToken())))
    }

    Pagination.offsetUnfoldSegmentEval(fetchPage)
  }
}
