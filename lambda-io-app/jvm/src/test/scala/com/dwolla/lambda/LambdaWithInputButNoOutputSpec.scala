package com.dwolla.lambda

import java.io._

import cats.effect._
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import io.chrisdavenport.log4cats.Logger
import natchez._
import natchez.Trace.kleisliInstance
import org.specs2.mutable.Specification
import io.circe.literal._

class LambdaWithInputButNoOutputSpec extends Specification {
  "Json-based instance" should {
    "read JSON input" in {
      val inputStream = new ByteArrayInputStream(
        json"""{
                 "foo": "Hello world"
               }""".noSpaces.getBytes())

      val outputStream = new OutputStream {
        override def write(b: Int): Unit = throw IntentionallyThrownException
      }

      val output = new InputWithoutOutputInstance().handleRequest(inputStream, outputStream, null)

      output must be_==(())
    }
  }
}

private[lambda] class InputWithoutOutputInstance extends IOLambda[Input, Unit] {
  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (s: Input, context: Context): F[LambdaResponse[Unit]] =
    for {
      _ <- Trace[F].span(s.foo) {
        if (s.foo == "Hello world") ().pure[F]
        else IntentionallyThrownException.raiseError[F, Unit]
      }
    } yield ()
}
