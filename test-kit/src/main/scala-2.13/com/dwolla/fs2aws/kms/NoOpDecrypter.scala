package com.dwolla.fs2aws.kms

import cats._
import fs2._

class NoOpDecrypter[F[_] : Applicative] extends KmsDecrypter[F] {
  override def decrypt[A](transformer: Transform[A], cryptoText: A): F[Array[Byte]] =
    Applicative[F].pure(transformer(cryptoText))

  override def decrypt[A](transform: Transform[A], cryptoTexts: (String, A)*): Stream[F, Map[String, Array[Byte]]] =
    Stream.emit(cryptoTexts.toMap.view.mapValues(transform).toMap)

  override def decryptBase64(cryptoTexts: (String, String)*): Stream[F, Map[String, Array[Byte]]] =
    Stream.emit(cryptoTexts.toMap.view.mapValues(base64DecodingTransform).toMap)
}
