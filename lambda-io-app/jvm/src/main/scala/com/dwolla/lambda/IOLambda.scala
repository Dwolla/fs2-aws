package com.dwolla.lambda

import java.io._

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import cats.implicits._
import com.amazonaws.services.lambda.runtime._
import io.circe._
import natchez._
import IOLambda._
import fs2.Stream
import fs2.io.writeOutputStream
import fs2.text.utf8Encode
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

/**
 * This class is analogous to the cats-effect `IOApp`, but for
 * AWS Lambda functions instead of standalone applications.
 *
 * Implementers can decide whether they want to read from the Lambda's
 * input or write to its output by picking concrete types for the `A`
 * and `B` type variables, respectively. If I/O is desired, the type
 * should have a Circe codec. If no I/O is desired, the type variable
 * should be set to `Unit`.
 *
 * For example, to read to the JSON ADT and avoid any output written
 * to the Lambda's OutputStream, one could use:
 *
 * {{{
 *   class JsonReadingLambda extends IOLambda[Json, Unit]
 * }}}
 *
 * Concrete classes must implement the `handleRequestF` method, which
 * is written using an abstract effect type. Instances of the
 * `Concurrent`, `ContextShift`, `Timer`, and `Trace` typeclasses are
 * made available implicitly, along with an explicit blocking
 * execution context. (`F` is generic so that the underlying
 * implementation can treat it as `Kleisli[IO, Span[IO], *]`, which
 * is also why `Concurrent` is the most powerful effect typeclass
 * available—no `Effect` is available for `Kleisli`.)
 *
 * IOLambda should be considered experimental at this point.
 */
abstract class IOLambda[A, B](printer: Printer = Defaults.printer,
                              executionContext: ExecutionContext = Defaults.executionContext,
                             )(implicit LR: LambdaReader[Kleisli[Kleisli[IO, Span[IO], *], LambdaReaderEnvironment[Kleisli[IO, Span[IO], *]], *], A]) extends RequestStreamHandler {
  protected implicit def contextShift: ContextShift[IO] = cats.effect.IO.contextShift(executionContext)
  protected implicit def timer: Timer[IO] = cats.effect.IO.timer(executionContext)
  protected implicit def logger: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("LambdaLogger")
  private implicit val kleisliLogger: Logger[Kleisli[IO, Span[IO], *]] = Logger[IO].mapK(Kleisli.liftK)

  def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                               (req: A, context: Context): F[LambdaResponse[B]]

  val tracingEntryPoint: Resource[IO, EntryPoint[IO]] = NoOpEntryPoint[IO]

  private def printToStream(b: B)(implicit encoder: Encoder[B]): Stream[IO, Byte] = {
    import io.circe.syntax._
    Stream.emit(printer.print(b.asJson))
      .through(utf8Encode[IO])
  }

  private def handleRequest(input: IO[InputStream], output: IO[OutputStream], context: Context): IO[Unit] =
    tracingEntryPoint.use {
      _.root("IOLambda").use { span =>
        Blocker[IO].use { blocker =>
          val response: IO[LambdaResponse[B]] =
            LR
              .read(Kleisli.liftF(Kleisli.liftF(input)))
              .run(LambdaReaderEnvironment(blocker))
              .flatMap(handleRequestF[Kleisli[IO, Span[IO], *]](blocker)(_, context))
              .run(span)

          Stream.eval(response).flatMap {
            case NoResponse =>
              Stream.empty
            case ResponseWrapper(resp, encoder: Encoder[B]) =>
              Stream.emit(resp)
                .flatMap(printToStream(_)(encoder))
          }.through(writeOutputStream(output, blocker))
            .compile
            .drain
        }
      }
    }

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit =
    handleRequest(Sync[IO].delay(input), Sync[IO].delay(output), context)
      .unsafeRunSync()
}

object IOLambda {
  object Defaults {
    val printer: Printer = Printer.noSpaces
    val executionContext: ExecutionContext = ExecutionContext.global
    val logRequest: Boolean = true
  }
}

object NoOpEntryPoint {
  private def noOpSpan[F[_] : Applicative]: Resource[F, Span[F]] = Resource.pure[F, Span[F]](new Span[F] {
    override def put(fields: (String, TraceValue)*): F[Unit] = ().pure[F]
    override def kernel: F[Kernel] = Kernel(Map.empty).pure[F]
    override def span(name: String): Resource[F, Span[F]] = noOpSpan[F]
  })

  def apply[F[_] : Applicative]: Resource[F, EntryPoint[F]] =
    Resource.pure[F, EntryPoint[F]](new EntryPoint[F] {
      override def root(name: String): Resource[F, Span[F]] = noOpSpan[F]
      override def continue(name: String, kernel: Kernel): Resource[F, Span[F]] = noOpSpan[F]
      override def continueOrElseRoot(name: String, kernel: Kernel): Resource[F, Span[F]] = noOpSpan[F]
    })
}
