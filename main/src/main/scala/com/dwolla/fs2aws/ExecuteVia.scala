package com.dwolla.fs2aws

import cats.effect._
import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.language.higherKinds

class ExecuteVia[F[_], Req <: AmazonWebServiceRequest](val req: Req) extends AnyVal {
  def apply[Res](awsAsyncFunction: AwsAsyncFunction[Req, Res])(implicit F: Async[F]): F[Res] = F.async[Res] { callback =>
    awsAsyncFunction(req, new AsyncHandler[Req, Res] {
      override def onError(exception: Exception): Unit = callback(Left(exception))
      override def onSuccess(request: Req, result: Res): Unit = callback(Right(result))
    })

    ()
  }
}
