package com.dwolla.lambda

import java.io._

import cats.effect._
import com.amazonaws.services.lambda.runtime._
import com.dwolla.lambda.IOLambda._
import io.circe._

import scala.concurrent.ExecutionContext

/**
 * This class is analogous to the cats-effect `IOApp`, but for
 * AWS Lambda functions instead of standalone applications.
 *
 * It assumes the Lambda's input is JSON that can be decoded
 * into the type `A`, and that the output type `B` should be
 * encoded back into JSON and written to the Lambda's
 * OutputStream.
 *
 * `IOLambda` should be considered experimental at this point.
 */
abstract class IOLambda[A: Decoder, B: Encoder](printer: Printer = Defaults.printer,
                                                logRequest: Boolean = Defaults.logRequest,
                                                executionContext: ExecutionContext = Defaults.executionContext) extends RequestStreamHandler {
  protected implicit def contextShift: ContextShift[IO] = cats.effect.IO.contextShift(executionContext)
  protected implicit def timer: Timer[IO] = cats.effect.IO.timer(executionContext)

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit =
    Blocker[IO]
      .use(IOLambda(_, this, printer, logRequest).handleRequestAndWriteResponse(input, output))
      .unsafeRunSync()

  def handleRequest(blocker: Blocker)(a: A): IO[Option[B]]
}

object IOLambda {
  object Defaults {
    val printer: Printer = Printer.noSpaces
    val executionContext: ExecutionContext = ExecutionContext.global
    val logRequest: Boolean = true
  }

  def apply[A: Decoder, B: Encoder](blocker: Blocker,
                                    ioLambda: IOLambda[A, B],
                                    printer: Printer,
                                    logRequest: Boolean)
                                   (implicit CS: ContextShift[IO]): JsonLambdaF[IO, A, B] =
    new JsonLambdaF[IO, A, B](blocker, printer, logRequest) {
      override def handleRequest(req: A): IO[Option[B]] = ioLambda.handleRequest(blocker)(req)
    }
}
