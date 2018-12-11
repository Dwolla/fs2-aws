package com.dwolla.fs2utils

import java.security.MessageDigest

import cats.effect._
import cats.effect.concurrent.Deferred
import com.dwolla.fs2utils.hashing._
import org.specs2.matcher._
import org.specs2.mutable.Specification
import fs2._
import org.apache.commons.codec.binary.Hex
import org.specs2.execute.Result

class Sha256PipeSpec extends Specification with IOMatchers {

  "hexStringPipe" should {
    "hex some things" >> {
      Stream.emit(0x2).map(_.toByte).chunks.through(hexStringPipe).compile.toList must beEqualTo("02".toCharArray.toList)
    }

    "calculate the hex string from the bytes of emoji" >> {
      val example = "👩‍💻"
      new String(Stream.emit(example).through(text.utf8Encode).chunks.through(hexStringPipe).compile.toList.toArray) must beEqualTo(Hex.encodeHexString(example.getBytes("UTF-8")))
    }
  }

  "Sha256Pipe" should {
    "pass the bytes through unchanged while returning the hash of the bytes" >> {
      val example = "Dwolla 🧟‍♀️"
      for {
        expectedDigest <- IO(MessageDigest.getInstance("SHA-256").digest(example.getBytes("UTF-8")))
        deferredDigest <- Deferred[IO, String]
        str <- Stream.emit(example).covary[IO]
                .through(text.utf8Encode)
                .through(Sha256Pipe(deferredDigest))
                .through(text.utf8Decode)
                .compile
                .toList
        calculatedDigest <- deferredDigest.get
      } yield {
        str must beEqualTo(List(example))
        calculatedDigest must beEqualTo(Hex.encodeHexString(expectedDigest))
      }
    }
  }

  implicit def ioToResult[T](io: IO[MatchResult[T]]): Result = io.unsafeRunSync().toResult
}
