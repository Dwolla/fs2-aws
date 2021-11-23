package com.dwolla.fs2utils

import cats.effect.IO
import fs2._
import com.eed3si9n.expecty.Expecty.expect
import munit.CatsEffectSuite

class PaginationSpec extends CatsEffectSuite {

  val unfoldFunction: Option[Int] => IO[(Chunk[Int], Option[Int])] =
    nextToken => {
      val offset = nextToken.fold(0)(identity)
      val ints = (3 * offset) until (3 * offset + 3)

      IO.pure((Chunk.seq(ints), if (offset < 3) Option(offset + 1) else None))
    }

  test("Pagination should unfold the given thing") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction).take(5)

    stream.compile.toList.map(output => expect(output == (0 until 5)))
  }

  test("Pagination should stop unfolding when None is returned as the token value") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction)

    stream.compile.toList.map(output => expect(output == (0 until 12)))
  }
}
