package com.dwolla.fs2aws.kms

import java.nio.ByteBuffer

import cats.effect._
import cats.implicits._
import com.amazonaws.regions.Regions
import com.amazonaws.services.kms._
import com.amazonaws.services.kms.model._
import com.dwolla.fs2aws._
import com.dwolla.fs2aws.kms.KmsDecrypter._
import fs2._

import scala.concurrent.ExecutionContext
import com.amazonaws.regions.Regions.US_WEST_2

import scala.language.higherKinds

class KmsDecrypter(val asyncClient: AWSKMSAsync) extends AnyVal {

  def decrypt[F[_] : Effect, A](transformer: Transform[A], cryptoText: A): F[Array[Byte]] = new DecryptRequest()
      .withCiphertextBlob(ByteBuffer.wrap(transformer(cryptoText)))
      .executeVia[F](asyncClient.decryptAsync)
      .map(_.getPlaintext.array())

  def decrypt[F[_] : Effect, A](transform: Transform[A], cryptoTexts: (String, A)*)
                               (implicit ec: ExecutionContext): Stream[F, Map[String, Array[Byte]]] =
    Stream.emits(cryptoTexts)
      .map {
        case (name, cryptoText) ⇒ decrypt(transform, cryptoText).map(name → _)
      }
      .map(Stream.eval)
      .join(10)
      .fold(Map.empty[String, Array[Byte]]) {
        case (map, tuple) ⇒ map + tuple
      }

  def decryptBase64[F[_] : Effect, A](cryptoTexts: (String, String)*)
                                     (implicit ec: ExecutionContext): Stream[F, Map[String, Array[Byte]]] = decrypt(base64DecodingTransform, cryptoTexts: _*)
}

object KmsDecrypter {
  type Transform[A] = A ⇒ Array[Byte]

  val noopTransform: Transform[Array[Byte]] = identity
  val base64DecodingTransform: Transform[String] = javax.xml.bind.DatatypeConverter.parseBase64Binary

  def stream[F[_]](region: Regions = US_WEST_2)(implicit F: Sync[F]): Stream[F, KmsDecrypter] =
    for {
      client ← Stream.bracket(F.delay(AWSKMSAsyncClientBuilder.standard().withRegion(region).build()))(Stream.emit(_), c ⇒ F.delay(c.shutdown()))
    } yield new KmsDecrypter(client)
}
