package com.dwolla.lambda

case object IntentionallyThrownException extends RuntimeException("Boom!", null, true, false)

private[lambda] case class Input(foo: String)

object Input {
  implicit val inputCodec: io.circe.Codec[Input] = io.circe.generic.semiauto.deriveCodec
}

private[lambda] case class Output(foo: String)

object Output {
  implicit val outputCodec: io.circe.Codec[Output] = io.circe.generic.semiauto.deriveCodec
}
