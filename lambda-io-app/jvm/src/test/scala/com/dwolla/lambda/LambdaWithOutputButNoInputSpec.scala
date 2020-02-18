package com.dwolla.lambda

import java.io._

import cats.effect._
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import io.chrisdavenport.log4cats.Logger
import natchez._
import org.specs2.mutable.Specification
import io.circe.parser._

class LambdaWithOutputButNoInputSpec extends Specification {
  "Json-based instance" should {
    "JSON output" in {
      val explodingInputStream = new InputStream {
        override def read(): Int = throw IntentionallyThrownException
      }
      val outputStream = new ByteArrayOutputStream()

      val output = new LambdaWithOutputButNoInput().handleRequest(explodingInputStream, outputStream, null)

      output must be_==(())

      val writtenOutput = parse(new String(outputStream.toByteArray)).flatMap(_.as[Output])
      writtenOutput must beRight(Output("Hello world"))
    }
  }
}

private[lambda] class LambdaWithOutputButNoInput extends IOLambda[Unit, Output] {
  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (s: Unit, context: Context): F[LambdaResponse[Output]] =
    for {
      _ <- Trace[F].span("LambdaWithOutputButNoInput") {
        s.pure[F]
      }
    } yield Output("Hello world")
}
