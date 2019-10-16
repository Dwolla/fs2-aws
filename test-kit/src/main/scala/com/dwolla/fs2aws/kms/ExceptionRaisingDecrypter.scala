package com.dwolla.fs2aws.kms

import _root_.fs2._
import cats._

class ExceptionRaisingDecrypter[F[_]](throwable: Throwable)(implicit F: ApplicativeError[F, Throwable]) extends KmsDecrypter[F] {
  override def decrypt[A](transformer: Transform[A], cryptoText: A): F[Array[Byte]] =
    ApplicativeError[F, Throwable].raiseError(throwable)

  override def decrypt[A](transform: Transform[A], cryptoTexts: (String, A)*): Stream[F, Map[String, Array[Byte]]] =
    Stream.raiseError(throwable)

  override def decryptBase64(cryptoTexts: (String, String)*): Stream[F, Map[String, Array[Byte]]] =
    Stream.raiseError(throwable)
}
