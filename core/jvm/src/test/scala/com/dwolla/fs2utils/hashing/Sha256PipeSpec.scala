package com.dwolla.fs2utils.hashing

import cats.effect._
import cats.implicits._
import com.eed3si9n.expecty.Expecty.expect
import fs2._
import munit.CatsEffectSuite

import java.security.MessageDigest
import scala.concurrent.duration._

class Sha256PipeSpec extends CatsEffectSuite {

  test("Sha256Pipe should pass the bytes through unchanged while returning the hash of the bytes") {
      val example = "Dwolla 🧟‍♀️"
      for {
        expectedDigest <- IO(MessageDigest.getInstance("SHA-256").digest(example.getBytes("UTF-8")))
        deferredDigest <- Deferred[IO, Either[Throwable, String]]
        str <- Stream.emit(example)
                .through(text.utf8.encode)
                .through(Sha256Pipe(deferredDigest))
                .through(text.utf8.decode)
                .compile
                .toList
        calculatedDigest <- deferredDigest.get
      } yield {
        expect(str == List(example))
        expect(calculatedDigest == Right(expectedDigest.toHexString))
      }
    }

    test("fail the deferred if the stream raises an exception") {
      for {
        deferred <- Deferred[IO, Either[Throwable, String]]
        exception = new RuntimeException("boom") {}
        failure <- (Stream.eval(IO("hello world")) ++ Stream.raiseError[IO](exception))
                      .through(text.utf8.encode)
                      .through(Sha256Pipe(deferred))
                      .compile
                      .drain
                      .attempt
        output <- deferred.get.flatMap(_.liftTo[IO]).timeout(2.seconds).attempt
      } yield {
        expect(failure == Left(exception))
        expect(output == Left(InputStreamFailed(exception)))
      }
  }
}
