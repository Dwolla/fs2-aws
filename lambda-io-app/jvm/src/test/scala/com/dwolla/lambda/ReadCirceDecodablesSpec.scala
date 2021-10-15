package com.dwolla.lambda

import java.io._

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import com.dwolla.lambda.LambdaReader._
import org.typelevel.log4cats.Logger
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import natchez.Trace.Implicits._
import org.scalacheck._
import org.specs2._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher._
import org.specs2.mutable.Specification

class ReadCirceDecodablesSpec(implicit ee: ExecutionEnv) extends Specification with Matchers with ScalaCheck {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ee.ec)

  "readCirceDecodables" should {
    "read and parse input larger than the chunk size" >> prop { foo: Foo =>
      (for {
        is <- IO(new ByteArrayInputStream(foo.asJson.noSpaces.getBytes()))
        output <- Blocker[IO]
          .map(new LambdaReaderEnvironment[IO](_, contextShift, new NoOpLogger[IO]))
          .use(readCirceDecodables[IO, Foo].read(Kleisli.pure(is)).run(_))
      } yield (output must_== foo))
        .unsafeRunSync()
    }
  }
}

case class Foo(bar: String)
object Foo {
  implicit val fooCodec: Codec[Foo] = deriveCodec

  private val bufferSizeMinusFooJsonPadding: Int = 4096 - Foo("").asJson.noSpaces.length + 1
  private val oneMegabyte: Int = 1048576

  implicit val fooGen: Arbitrary[Foo] = Arbitrary(
    for {
      size <- Gen.chooseNum(bufferSizeMinusFooJsonPadding, oneMegabyte)
      charList <- Gen.listOfN(size, Gen.alphaNumChar)
    } yield Foo(charList.mkString)
  )
}

class NoOpLogger[F[_] : Applicative] extends Logger[F] {
  override def error(message: => String): F[Unit] = ().pure[F]
  override def warn(message: =>String): F[Unit] = ().pure[F]
  override def info(message: =>String): F[Unit] = ().pure[F]
  override def debug(message: =>String): F[Unit] = ().pure[F]
  override def trace(message: =>String): F[Unit] = ().pure[F]
  override def error(t: Throwable)(message: =>String): F[Unit] = ().pure[F]
  override def warn(t: Throwable)(message: =>String): F[Unit] = ().pure[F]
  override def info(t: Throwable)(message: =>String): F[Unit] = ().pure[F]
  override def debug(t: Throwable)(message: =>String): F[Unit] = ().pure[F]
  override def trace(t: Throwable)(message: =>String): F[Unit] = ().pure[F]
}
