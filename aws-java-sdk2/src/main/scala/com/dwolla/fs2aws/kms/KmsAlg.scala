package com.dwolla.fs2aws.kms

import com.dwolla.fs2aws.AwsEval._
import cats.effect._
import cats.implicits._
import cats.tagless._
import software.amazon.awssdk.services.kms._
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kms.model.DecryptRequest
import software.amazon.awssdk.utils.BinaryUtils

import scala.util.Try

@autoFunctorK
@autoInstrument
trait KmsAlg[F[_]] {
  def decrypt(string: String): F[String]
}

object KmsAlg {
  private def acquireKmsClient[F[_] : Sync]: F[KmsAsyncClient] =
    Sync[F].delay(KmsAsyncClient.builder().build())

  private def releaseKmsClient[F[_] : Sync](client: KmsAsyncClient): F[Unit] =
    Sync[F].delay(client.close())

  def resource[F[_] : Concurrent]: Resource[F, KmsAlg[F]] =
    for {
      client <- Resource.make(acquireKmsClient[F])(releaseKmsClient[F])
    } yield new KmsAlg[F] {
      private def buildRequest(ciphertextBlob: SdkBytes) =
        DecryptRequest.builder().ciphertextBlob(ciphertextBlob).build()

      override def decrypt(string: String): F[String] =
        for {
          ciphertextBlob <- Try(SdkBytes.fromByteArray(BinaryUtils.fromBase64(string))).liftTo[F]
          resp <- eval[F](buildRequest(ciphertextBlob))(client.decrypt)(_.plaintext().asUtf8String())
        } yield resp

    }
}
