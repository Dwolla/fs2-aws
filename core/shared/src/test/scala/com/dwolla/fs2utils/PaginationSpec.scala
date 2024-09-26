package com.dwolla.fs2utils

import cats.*
import cats.arrow.FunctionK
import cats.effect.IO
import cats.syntax.all.*
import com.dwolla.fs2utils.ArbitraryEffect.genArbitraryEffect
import fs2.*
import munit.*
import org.scalacheck.effect.PropF
import org.scalacheck.{Arbitrary, Gen}

class PaginationSpec extends CatsEffectSuite {
  private def unfoldFunction[F[_] : Applicative](nextToken: Option[Long]): F[(Chunk[Long], Option[Long])] = {
    val offset = nextToken.getOrElse(0L)
    val ints = (3 * offset) until (3 * offset + 3)

    (Chunk.from(ints), if (offset < 3) Option(offset + 1) else None).pure[F]
  }

  test("Pagination should unfold the given thing up to an arbitrary take limit") {
    PropF.forAllF(genArbitraryEffect, Gen.choose(0, 12)) { (effect: ArbitraryEffect, takeLimit: Int) =>
      type F[a] = effect.F[a]
      implicit val ef: ArbitraryEffect.Aux[F] = effect

      val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[F]).take(takeLimit.toLong)
      effect.toIO(stream.compile.toList.map(assertEquals(_, 0 until takeLimit)))
    }
  }

  test("Pagination should stop unfolding when None is returned as the token value") {
    PropF.forAllF { (effect: ArbitraryEffect) =>
      type F[a] = effect.F[a]
      implicit val ef: ArbitraryEffect.Aux[F] = effect

      val stream = Pagination.offsetUnfoldChunkEval(unfoldFunction[F])
      effect.toIO(stream.compile.toList.map(assertEquals(_, 0 until 12)))
    }
  }

  private implicit def compareListAndRange[A: Integral]: Compare[List[A], Range] = new Compare[List[A], Range] {
    override def isEqual(obtained: List[A], expected: Range): Boolean =
      obtained.map(implicitly[Integral[A]].toInt) == expected.toList
  }
}

sealed trait ArbitraryEffect {
  type F[_]
  val applicative: Applicative[F]
  val compiler: Compiler[F, F]
  val toIO: F ~> IO
}

object ArbitraryEffect {
  type Aux[FF[_]] = ArbitraryEffect { type F[a] = FF[a] }

  val idInstance: ArbitraryEffect.Aux[Id] = new ArbitraryEffect {
    override type F[a] = Id[a]
    override val applicative: Applicative[F] = implicitly
    override val compiler: Compiler[F, F] = implicitly
    override val toIO: Id ~> IO = new (Id ~> IO) {
      override def apply[A](fa: Id[A]): IO[A] = IO.pure(fa)
    }
  }

  val ioInstance: ArbitraryEffect.Aux[IO] = new ArbitraryEffect {
    override type F[a] = IO[a]
    override val applicative: Applicative[F] = implicitly
    override val compiler: Compiler[F, F] = implicitly
    override val toIO: IO ~> IO = FunctionK.id
  }

  val genArbitraryEffect: Gen[ArbitraryEffect] = Gen.oneOf(idInstance, ioInstance)
  implicit val arbitraryEffect: Arbitrary[ArbitraryEffect] = Arbitrary(genArbitraryEffect)

  implicit def applicativeFromEffectHolder[F[_]](implicit effect: ArbitraryEffect.Aux[F]): Applicative[F] = effect.applicative
  implicit def streamCompilerFromEffectHolder[F[_]](implicit effect: ArbitraryEffect.Aux[F]): Compiler[F, F] = effect.compiler
}
