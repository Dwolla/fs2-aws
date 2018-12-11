package com.dwolla.fs2utils.hashing

import java.security.MessageDigest

import cats.effect._
import cats.effect.concurrent.Deferred
import fs2._

object Sha256Pipe {
  def apply[F[_] : Sync](promisedHexString: Deferred[F, String]): Pipe[F, Byte, Byte] = {
    def pull(digest: MessageDigest): Stream[F, Byte] => Pull[F, Byte, String] =
      _.pull.uncons.flatMap {
        case None =>
          Pull.eval(Sync[F].delay(digest.digest())).map(_.toHexString)
        case Some((c: Chunk[Byte], rest: Stream[F, Byte])) =>
          val bytes = c.toBytes
          for {
            _ <- Pull.eval(Sync[F].delay(digest.update(bytes.values, bytes.offset, bytes.length)))
            _ <- Pull.output(c)
            hexString <- pull(digest)(rest)
          } yield hexString
      }

    def calculateHashOf(input: Stream[F, Byte]): Pull[F, Byte, Unit] =
      for {
        initialDigest <- Pull.eval(Sync[F].delay(MessageDigest.getInstance("SHA-256")))
        hexString <- pull(initialDigest)(input)
        _ <- Pull.eval(promisedHexString.complete(hexString))
      } yield ()

    calculateHashOf(_).stream
  }
}
