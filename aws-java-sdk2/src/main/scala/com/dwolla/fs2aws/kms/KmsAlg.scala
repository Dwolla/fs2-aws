package com.dwolla.fs2aws.kms

import cats.effect.*
import cats.implicits.*
import cats.tagless.*
import cats.tagless.aop.Instrument
import cats.~>
import com.dwolla.fs2aws.AwsEval.*
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kms.*
import software.amazon.awssdk.services.kms.model.DecryptRequest
import software.amazon.awssdk.utils.BinaryUtils

import scala.util.Try

trait KmsAlg[F[_]] {
  def decrypt(string: String): F[String]
}

object KmsAlg {
  implicit val instrument: Instrument[KmsAlg] = Derive.instrument

  private def acquireKmsClient[F[_] : Sync]: F[KmsAsyncClient] =
    Sync[F].delay(KmsAsyncClient.builder().build())

  private def releaseKmsClient[F[_] : Sync](client: KmsAsyncClient): F[Unit] =
    Sync[F].delay(client.close())

  def resource[F[_] : Async]: Resource[F, KmsAlg[F]] =
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


  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  def instrumentForKmsAlg(): Instrument[KmsAlg] = instrument

  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  def mapK[F[_], G[_]](alg: KmsAlg[F], fk: F ~> G): KmsAlg[G] = instrument.mapK(alg)(fk)

  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  def functorKForKmsAlg(): FunctorK[KmsAlg] = instrument

  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  object fullyRefined {
    @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
    implicit val functorKForFullyRefinedKmsAlg: FunctorK[_root_.com.dwolla.fs2aws.kms.KmsAlg] = functorKForKmsAlg

    @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
    object autoDerive {
      @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
      @_root_.java.lang.SuppressWarnings(value = _root_.scala.Array("org.wartremover.warts.ImplicitParameter"))
      implicit def fromFunctorKFullyRefinedKmsAlg[F[_], G[_]](implicit fk: F ~> G, af: KmsAlg[F]): KmsAlg[G] = mapK(af, fk)
    }
  }

  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  object autoDerive {
    @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
    @_root_.java.lang.SuppressWarnings(value = _root_.scala.Array("org.wartremover.warts.ImplicitParameter"))
    implicit def fromFunctorKKmsAlg[F[_], G[_]](implicit fk: F ~> G, af: KmsAlg[F]): KmsAlg[G] = mapK(af, fk)
  }
}
