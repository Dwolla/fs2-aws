package com.dwolla.lambda

import _root_.fs2.text.{utf8Decode, utf8Encode}
import cats._
import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import _root_.fs2._

abstract class JsonLambdaF[F[_] : Sync : ContextShift, A: Decoder, B: Encoder](blocker: Blocker,
                                                                               printer: Printer = Printer.noSpaces,
                                                                               logRequest: Boolean = true) extends LambdaF[F](blocker) {
  def handleRequest(req: A): F[Option[B]]

  protected implicit def logger: Logger[F] = Slf4jLogger.getLoggerFromName[F]("LambdaLogger")
  private val logRequestF: F[Boolean] = logRequest.pure[F]

  private val readFrom: Stream[F, Byte] => F[String] =
    _.through(utf8Decode[F])
      .compile
      .lastOrError

  private def printToStream(b: B): Stream[F, Byte] =
    Stream.emit(printer.print(b.asJson))
      .through(utf8Encode[F])

  private def writeOutput(maybeB: Option[B]): Stream[F, Byte] =
    Stream.emits(maybeB.toSeq)
      .flatMap(printToStream)

  private def parseStringLoggingErrors(str: String): F[Json] =
    parse(str)
      .toEitherT[F]
      .leftSemiflatTap(Logger[F].error(_)(s"Could not parse the following input:\n$str"))
      .rethrowT
      .flatTap(logJsonIfEnabled)

  private def logJsonIfEnabled(json: Json): F[Unit] =
    logRequestF.ifA(Logger[F].info(
      s"""Received input:
         |${printer.print(json)}""".stripMargin), Applicative[F].unit)

  private def parseStream(input: Stream[F, Byte]): F[A] =
    for {
      str <- readFrom(input)
      json <- parseStringLoggingErrors(str)
      req <- json.as[A].liftTo[F]
    } yield req

  override val run: Pipe[F, Byte, Byte] = input =>
    Stream.eval(parseStream(input) >>= handleRequest) >>= writeOutput

}
