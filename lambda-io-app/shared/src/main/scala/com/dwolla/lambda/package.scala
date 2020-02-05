package com.dwolla

import cats._
import cats.data._
import cats.implicits._
import io.circe._

package object lambda extends LowPriorityLambdaResponseImplicits {
  implicit class LeftSemiflatTap[F[_], A, B](private val eitherT: EitherT[F, A, B]) extends AnyVal {
    def leftSemiflatTap[C](f: A => F[C])(implicit M: Monad[F]): EitherT[F, A, B] =
      eitherT.leftSemiflatMap(x => f(x) map (_ => x))
  }

  implicit val unitToResponseWrapper: Unit => LambdaResponse[Nothing] = _ => NoResponse
}

package lambda {
  trait LowPriorityLambdaResponseImplicits {
    implicit def encodableToResponseWrapper[T: Encoder](t: T): LambdaResponse[T] =
      ResponseWrapper(t, Encoder[T])
  }

  sealed trait LambdaResponse[+T]
  case object NoResponse extends LambdaResponse[Nothing]
  case class ResponseWrapper[T](response: T, encoder: Encoder[T]) extends LambdaResponse[T]
}
