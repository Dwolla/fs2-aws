package com.dwolla.fs2aws.kms

import java.nio.ByteBuffer

import cats.effect._
import cats.implicits._
import com.amazonaws.regions.Regions
import com.amazonaws.regions.Regions.US_WEST_2
import com.amazonaws.services.kms._
import com.amazonaws.services.kms.model._
import com.dwolla.fs2aws._
import fs2._

import scala.language.higherKinds

trait KmsDecrypter[F[_]] {
  def decrypt[A](transformer: Transform[A], cryptoText: A): F[Array[Byte]]
  def decrypt[A](transform: Transform[A], cryptoTexts: (String, A)*): Stream[F, Map[String, Array[Byte]]]
  def decryptBase64(cryptoTexts: (String, String)*): Stream[F, Map[String, Array[Byte]]]
}

class KmsDecrypterImpl[F[_] : ConcurrentEffect](asyncClient: AWSKMSAsync) extends KmsDecrypter[F] {

  def decrypt[A](transformer: Transform[A], cryptoText: A): F[Array[Byte]] = new DecryptRequest()
    .withCiphertextBlob(ByteBuffer.wrap(transformer(cryptoText)))
    .executeVia[F](asyncClient.decryptAsync)
    .map(_.getPlaintext.array())

  def decrypt[A](transform: Transform[A], cryptoTexts: (String, A)*): Stream[F, Map[String, Array[Byte]]] =
    Stream.emits(cryptoTexts)
      .map {
        case (name, cryptoText) ⇒ decrypt(transform, cryptoText).map(name → _)
      }
      .map(Stream.eval)
      .parJoin(10)
      .fold(Map.empty[String, Array[Byte]]) {
        case (map, tuple) ⇒ map + tuple
      }

  def decryptBase64(cryptoTexts: (String, String)*): Stream[F, Map[String, Array[Byte]]] =
    decrypt(base64DecodingTransform, cryptoTexts: _*)
}

object KmsDecrypter {
  def stream[F[_] : ConcurrentEffect](region: Regions = US_WEST_2): Stream[F, KmsDecrypter[F]] =
    for {
      client ← Stream.bracket(Sync[F].delay(AWSKMSAsyncClientBuilder.standard().withRegion(region).build()))(c ⇒ Sync[F].delay(c.shutdown()))
    } yield new KmsDecrypterImpl[F](client)
}
