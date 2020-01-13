package com.dwolla.lambda

import cats.effect._
import typings.awsDashLambda.awsDashLambdaMod.{Context, Handler}

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

trait IOLambda[A <: js.Any, B] {
  private implicit val executionContext: ExecutionContext = ExecutionContext.global
  protected implicit def contextShift: ContextShift[IO] = cats.effect.IO.contextShift(executionContext)
  protected implicit def timer: Timer[IO] = cats.effect.IO.timer(executionContext)

  def handleRequest(a: A, context: Context): IO[B]

  final val handler: Handler[A, B] = (a, context, _) =>
    handleRequest(a, context).unsafeToFuture().toJSPromise
}
