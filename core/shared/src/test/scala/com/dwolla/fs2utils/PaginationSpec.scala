package com.dwolla.fs2utils

import cats.effect.IO
import fs2._
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification

class PaginationSpec extends Specification with IOMatchers {

  val unfoldFunction: Option[Int] => IO[(Chunk[Int], Option[Int])] =
    nextToken => {
      val offset = nextToken.fold(0)(identity)
      val ints = (3 * offset) until (3 * offset + 3)

      IO.pure((Chunk.seq(ints), if (offset < 3) Option(offset + 1) else None))
    }

  "Pagination" should {
    "unfold the given thing" >> {
      val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction).take(5)

      stream.compile.toList must returnValue(equalTo(0 until 5))
    }

    "stop unfolding when None is returned as the token value" >> {
      val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction)

      stream.compile.toList must returnValue(equalTo(0 until 12))
    }
  }

}
