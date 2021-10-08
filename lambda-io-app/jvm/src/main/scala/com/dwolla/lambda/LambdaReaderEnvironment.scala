package com.dwolla.lambda

import java.io._

import _root_.fs2.io.readInputStream
import cats.Applicative
import cats.effect._
import cats.implicits._
import org.typelevel.log4cats.Logger
import natchez._

import scala.annotation.implicitNotFound

class LambdaReaderEnvironment[F[_]](val blocker: Blocker,
                                    val cs: ContextShift[F],
                                    val l: Logger[F])

object LambdaReaderEnvironment {
  def apply[F[_]](blocker: Blocker)(implicit CS: ContextShift[F], L: Logger[F]): LambdaReaderEnvironment[F] =
    new LambdaReaderEnvironment(blocker, CS, L)

  def unapply[F[_]](env: LambdaReaderEnvironment[F]): Option[(Blocker, ContextShift[F], Logger[F])] =
    Option((env.blocker, env.cs, env.l))
}

@implicitNotFound("Error resolving LambdaReader: Is there an io.circe.Decoder[${A}] in implicit scope?")
trait LambdaReader[F[_], A] {
  def read(is: F[InputStream]): F[A]
}

object LambdaReader extends LambdaReaderImplicits {
  def apply[F[_], A](implicit ev: LambdaReader[F, A]): ev.type = ev
}

trait LowPriorityLambdaReader {
  import cats.data._
  import io.circe._
  import io.circe.parser._
  import fs2._
  import fs2.text._

  implicit def readCirceDecodables[F[_] : Sync : Trace, A: Decoder]: LambdaReader[Kleisli[F, LambdaReaderEnvironment[F], *], A] =
    new LambdaReader[Kleisli[F, LambdaReaderEnvironment[F], *], A] {
      private val readFrom: Stream[F, Byte] => F[String] = s => Trace[F].span("readCirceDecodables.readFrom") {
        s.through(utf8Decode[F])
          .compile
          .string
      }

      private def parseStream(input: Stream[F, Byte])(implicit L: Logger[F]): F[A] = Trace[F].span("readCirceDecodables.parseStream") {
        for {
          str <- readFrom(input)
          json <- parseStringLoggingErrors(str)
          req <- json.as[A].liftTo[F]
        } yield req
      }

      private def parseStringLoggingErrors(str: String)(implicit L: Logger[F]): F[Json] = Trace[F].span("readCirceDecodables.parseStringLoggingErrors") {
        parse(str)
          .toEitherT[F]
          .leftSemiflatTap(Logger[F].error(_)(s"Could not parse the following input:\n$str"))
          .rethrowT
      }

      override def read(is: Kleisli[F, LambdaReaderEnvironment[F], InputStream]): Kleisli[F, LambdaReaderEnvironment[F], A] =
        is.tapWith { (env: LambdaReaderEnvironment[F], i: InputStream) => (env.blocker, env.cs, env.l, i) }
          .flatMapF {
            case (b, cs, l, i) =>
              implicit val CS: ContextShift[F] = cs
              implicit val L: Logger[F] = l
              parseStream(readInputStream(i.pure[F], 4096, b))
          }
    }
}

trait LambdaReaderImplicits extends LowPriorityLambdaReader {
  implicit def noInputLambdaReader[F[_] : Applicative]: LambdaReader[F, Unit] = _ => ().pure[F]
}
