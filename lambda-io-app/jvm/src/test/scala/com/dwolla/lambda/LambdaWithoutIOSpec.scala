package com.dwolla.lambda

import java.io._

import cats.effect._
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import org.typelevel.log4cats.Logger
import natchez._
import org.specs2.mutable.Specification

class NoInputInstanceSpec extends Specification {

  "NoIO instance" should {
    "not read from input" in {
      val explodingInputStream = new InputStream {
        override def read(): Int = throw IntentionallyThrownException
      }

      val outputStream = new ByteArrayOutputStream()

      val context: Context = null

      val output = new NoIOInstance().handleRequest(explodingInputStream, outputStream, context)

      output must be_==(())
    }

    "not write to output" in {
      val explodingInputStream = new ByteArrayInputStream(Array.empty)

      val outputStream = new OutputStream {
        override def write(b: Int): Unit = throw IntentionallyThrownException
      }

      val context: Context = null

      val output = new NoIOInstance().handleRequest(explodingInputStream, outputStream, context)

      output must be_==(())
    }
  }
}

private[lambda] class NoIOInstance extends IOLambda[Unit, Unit] {
  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (req: Unit, context: Context): F[LambdaResponse[Unit]] =
    NoResponse.pure[F].widen
}
