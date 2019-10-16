package com.dwolla

import cats._
import cats.data._
import cats.implicits._

package object lambda {
  private[lambda] implicit class LeftSemiflatTap[F[_], A, B](private val eitherT: EitherT[F, A, B]) extends AnyVal {
    def leftSemiflatTap[C](f: A => F[C])(implicit M: Monad[F]): EitherT[F, A, B] =
      eitherT.leftSemiflatMap(x => f(x) map (_ => x))
  }
}
