package com.dwolla.fs2aws

import cats.effect._
import fs2.Stream

import scala.language.higherKinds

class FetchAll[F[_], Req <: PaginatedRequest](val requestFactory: () ⇒ Req) extends AnyVal {
  def apply[Res <: PaginatedResult, T](awsAsyncFunction: AwsAsyncFunction[Req, Res])
                                      (extractor: Res ⇒ Seq[T])
                                      (implicit F: Effect[F]): Stream[F, T] =
    new PaginatedAwsClient(requestFactory).via(awsAsyncFunction)(extractor)
}
