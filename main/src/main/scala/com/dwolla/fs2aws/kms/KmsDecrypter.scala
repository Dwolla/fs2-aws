package com.dwolla.fs2aws.kms

import java.nio.ByteBuffer

import cats.data.Kleisli
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

class KmsDecrypterImpl[F[_] : Concurrent](asyncClient: AWSKMSAsync) extends KmsDecrypter[F] {
  def decrypt[A](transformer: Transform[A], cryptoText: A): F[Array[Byte]] =
    decrypt[A]((transformer, cryptoText))

  def decrypt[A](transform: Transform[A], cryptoTexts: (String, A)*): Stream[F, Map[String, Array[Byte]]] =
    Stream.emits(cryptoTexts)
      .map {
        case (name, cryptoText) => decrypt(transform, cryptoText).map(name -> _)
      }
      .map(Stream.eval)
      .parJoin(10)
      .fold(Map.empty[String, Array[Byte]]) {
        case (map, tuple) => map + tuple
      }

  def decryptBase64(cryptoTexts: (String, String)*): Stream[F, Map[String, Array[Byte]]] =
    decrypt(base64DecodingTransform, cryptoTexts: _*)

  private def buildDecryptionRequest[A]: Kleisli[F, (Transform[A], A), DecryptRequest] =
    Kleisli { case (transformer, cryptoText) =>
      Sync[F].delay(new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(transformer(cryptoText))))
    }

  private def executeDecryptionRequest: Kleisli[F, DecryptRequest, Array[Byte]] = Kleisli {
    _.executeVia[F](asyncClient.decryptAsync)
      .map(_.getPlaintext.array())
  }

  private def decrypt[A]: Kleisli[F, (Transform[A], A), Array[Byte]] =
    buildDecryptionRequest[A] andThen executeDecryptionRequest
}

object KmsDecrypter {
  private def acquireKmsClient[F[_] : Sync](region: Regions): F[AWSKMSAsync] =
    Sync[F].delay(AWSKMSAsyncClientBuilder.standard().withRegion(region).build())

  private def shutdownKmsClient[F[_] : Sync](client: AWSKMSAsync): F[Unit] =
    Sync[F].delay(client.shutdown())

  def resource[F[_] : Concurrent](region: Regions = US_WEST_2): Resource[F, KmsDecrypter[F]] =
    Resource
      .make(acquireKmsClient(region))(shutdownKmsClient[F])
      .map(new KmsDecrypterImpl[F](_))

  def stream[F[_] : Concurrent](region: Regions = US_WEST_2): Stream[F, KmsDecrypter[F]] =
    Stream.resource(KmsDecrypter.resource(region))
}
