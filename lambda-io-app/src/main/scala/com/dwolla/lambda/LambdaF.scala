package com.dwolla.lambda

import java.io._

import _root_.fs2.{Pipe, Stream}
import _root_.fs2.io.{readInputStream, writeOutputStream}
import cats.effect._

abstract class LambdaF[F[_] : Sync : ContextShift](blocker: Blocker) {
  val run: Pipe[F, Byte, Byte]

  private def readStream(inputStream: InputStream): Stream[F, Byte] =
    readInputStream(Sync[F].delay(inputStream), 4096, blocker)

  private def writeTo(outputStream: OutputStream): Stream[F, Byte] => Stream[F, Unit] =
    writeOutputStream(Sync[F].delay(outputStream), blocker)

  def handleRequestAndWriteResponse(inputStream: InputStream, outputStream: OutputStream): F[Unit] =
    readStream(inputStream)
      .through(run)
      .through(writeTo(outputStream))
      .compile
      .drain
}
