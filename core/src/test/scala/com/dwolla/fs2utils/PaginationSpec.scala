package com.dwolla.fs2utils

import cats.effect.IO
import fs2._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification

class PaginationSpec(implicit ee: ExecutionEnv) extends Specification with Matchers {

  val unfoldFunction: Option[Int] ⇒ IO[(Segment[Int, Unit], Option[Int])] =
    nextToken ⇒ {
      val offset = nextToken.fold(0)(identity)
      val ints = (3 * offset) until (3 * offset + 3)

      IO.pure((Segment.seq(ints), if (offset < 3) Option(offset + 1) else None))
    }

  "Pagination" should {
    "unfold the given thing" >> {
      val list = Pagination.offsetUnfoldSegmentEval(unfoldFunction).take(5)

      list.compile.toList.unsafeToFuture() must be_==(0 until 5).await
    }

    "stop unfolding when None is returned as the token value" >> {
      val list = Pagination.offsetUnfoldSegmentEval(unfoldFunction)

      list.compile.toList.unsafeToFuture() must be_==(0 until 12).await
    }
  }

}
