package com.dwolla.lambda

import java.io._

import cats.effect._
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import org.typelevel.log4cats.Logger
import natchez._
import org.specs2.mutable.Specification
import io.circe.literal._
import io.circe.parser._

class LambdaWithInputAndOutputSpec extends Specification {
  "Json-based instance" should {
    "read and write JSON input and output" in {
      val inputStream = new ByteArrayInputStream(
        json"""{
                 "foo": "Hello world"
               }""".noSpaces.getBytes())

      val outputStream = new ByteArrayOutputStream()

      val output = new TestInstance().handleRequest(inputStream, outputStream, null)

      output must be_==(())

      val writtenOutput = parse(new String(outputStream.toByteArray)).flatMap(_.as[Output])
      writtenOutput must beRight(Output("Hello world"))
    }
  }
}

private[lambda] class TestInstance extends IOLambda[Input, Output] {
  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (s: Input, context: Context): F[LambdaResponse[Output]] =
    for {
      _ <- Trace[F].span(s.foo) {
        ().pure[F]
      }
    } yield Output(s.foo)
}
