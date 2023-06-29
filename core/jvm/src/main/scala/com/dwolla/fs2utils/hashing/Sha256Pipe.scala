package com.dwolla.fs2utils.hashing

import cats.effect.*
import fs2.*
import scodec.bits.ByteVector

import java.security.MessageDigest

object Sha256Pipe {
  def apply[F[_] : Sync](promisedHexString: Deferred[F, Either[Throwable, String]]): Pipe[F, Byte, Byte] = {
    def pull(digest: MessageDigest): Stream[F, Byte] => Pull[F, Byte, String] =
      _.pull.uncons.flatMap {
        case None =>
          Pull.eval(Sync[F].delay(digest.digest())).map(_.toHexString)
        case Some((c: Chunk[Byte], rest: Stream[F, Byte])) =>
          val bytes: ByteVector = c.toByteVector
          for {
            _ <- Pull.eval(Sync[F].delay(digest.update(bytes.toByteBuffer)))
            _ <- Pull.output(c)
            hexString <- pull(digest)(rest)
          } yield hexString
      }

    def calculateHashOf(input: Stream[F, Byte]): Pull[F, Byte, Unit] =
      for {
        initialDigest <- Pull.eval(Sync[F].delay(MessageDigest.getInstance("SHA-256")))
        hexString <- pull(initialDigest)(input)
        _ <- Pull.eval(promisedHexString.complete(Right(hexString)))
      } yield ()

    calculateHashOf(_)
      .stream
      .handleErrorWith { ex =>
        Stream.eval(promisedHexString.complete(Left(InputStreamFailed(ex)))).drain ++ Stream.raiseError[F](ex)
      }
  }
}

case class InputStreamFailed(cause: Throwable) extends RuntimeException("The input stream failed, so no hash is available", cause)
