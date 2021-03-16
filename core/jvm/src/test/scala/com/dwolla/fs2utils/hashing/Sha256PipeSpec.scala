package com.dwolla.fs2utils.hashing

import java.security.MessageDigest

import cats.effect._
import cats.effect.concurrent.Deferred
import cats.implicits._
import fs2._
import org.specs2.execute.AsResult
import org.specs2.matcher._
import org.specs2.mutable.Specification
import org.specs2.specification.core.{AsExecution, Execution}

import scala.concurrent.duration._

class Sha256PipeSpec extends Specification with IOMatchers {

  "Sha256Pipe" should {
    "pass the bytes through unchanged while returning the hash of the bytes" >> {
      val example = "Dwolla 🧟‍♀️"
      for {
        expectedDigest <- IO(MessageDigest.getInstance("SHA-256").digest(example.getBytes("UTF-8")))
        deferredDigest <- Deferred[IO, Either[Throwable, String]]
        str <- Stream.emit(example)
                .through(text.utf8Encode)
                .through(Sha256Pipe(deferredDigest))
                .through(text.utf8Decode)
                .compile
                .toList
        calculatedDigest <- deferredDigest.get
      } yield {
        str must beEqualTo(List(example))
        calculatedDigest must beRight(expectedDigest.toHexString)
      }
    }

    "fail the deferred if the stream raises an exception" >> {
      for {
        deferred <- Deferred[IO, Either[Throwable, String]]
        exception = new RuntimeException("boom") {}
        failure <- (Stream.eval(IO("hello world")) ++ Stream.raiseError[IO](exception))
                      .through(text.utf8Encode)
                      .through(Sha256Pipe(deferred))
                      .compile
                      .drain
                      .attempt
        output <- deferred.get.flatMap(_.liftTo[IO]).timeout(2.seconds).attempt
      } yield {
        failure must beLeft(exception)
        output must beLeft(InputStreamFailed(exception))
      }
    }
  }

  private implicit def ioAsExecution[R: AsResult]: AsExecution[IO[R]] = new AsExecution[IO[R]] {
    def execute(r: => IO[R]): Execution = Execution.withEnvAsync(env => (IO.shift(env.executionContext) >> r).unsafeToFuture())
  }
}
