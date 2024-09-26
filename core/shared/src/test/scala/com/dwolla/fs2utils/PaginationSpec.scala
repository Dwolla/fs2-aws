package com.dwolla.fs2utils

import cats.*
import cats.effect.IO
import cats.syntax.all.*
import fs2.*
import munit.*

class PaginationSpec extends CatsEffectSuite {
  private def unfoldFunction[F[_] : Applicative](nextToken: Option[Long]): F[(Chunk[Long], Option[Long])] = {
    val offset = nextToken.getOrElse(0L)
    val ints = (3 * offset) until (3 * offset + 3)

    (Chunk.from(ints), if (offset < 3) Option(offset + 1) else None).pure[F]
  }

  test("Pagination should unfold the given thing in a pure stream") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[Id]).take(5)

    assertEquals(stream.compile.toList, 0 until 5)
  }

  test("Pagination should unfold the given thing in a pure stream") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[Id]).take(5)

    assertEquals(stream.compile.toList, 0 until 5)
  }

  test("Pagination should stop unfolding when None is returned as the token value in a pure stream") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[Id])

    assertEquals(stream.compile.toList, 0 until 12)
  }

  test("Pagination should unfold the given thing in an effectual stream") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[IO]).take(5)

    stream.compile.toList.map(assertEquals(_, 0 until 5))
  }

  test("Pagination should stop unfolding when None is returned as the token value in an effectual stream") {
    val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[IO])

    stream.compile.toList.map(assertEquals(_, 0 until 12))
  }

  private implicit def compareListAndRange[A: Integral]: Compare[List[A], Range] = new Compare[List[A], Range] {
    override def isEqual(obtained: List[A], expected: Range): Boolean =
      obtained.map(implicitly[Integral[A]].toInt) == expected.toList
  }
}
