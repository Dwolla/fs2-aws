package com.dwolla.fs2aws.kms

import cats.effect.*
import cats.syntax.all.*
import cats.tagless.*
import cats.tagless.aop.*
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
  implicit def instrumentForKmsAlg(): Instrument[KmsAlg] = instrument

  // do this manually until cats-tagless-macros are published for Scala 3
  private val instrument = new Instrument[KmsAlg] {
    override def instrument[F[_]](af: KmsAlg[F]): KmsAlg[Instrumentation[F, *]] = new KmsAlg[Instrumentation[F, *]] {
      override def decrypt(string: String): Instrumentation[F, String] =
        Instrumentation(af.decrypt(string), "KmsAlg", "decrypt")
    }

    override def mapK[F[_], G[_]](af: KmsAlg[F])(fk: F ~> G): KmsAlg[G] = new KmsAlg[G] {
      override def decrypt(string: String): G[String] = fk(af.decrypt(string))
    }
  }

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
  def mapK[F[_], G[_]](alg: KmsAlg[F], fk: F ~> G): KmsAlg[G] = instrumentForKmsAlg().mapK(alg)(fk)

  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  def functorKForKmsAlg(): FunctorK[KmsAlg] = instrumentForKmsAlg()

  @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
  object fullyRefined {
    @deprecated("only kept for binary compatibility purposes", "3.0.0-RC2")
    implicit val functorKForFullyRefinedKmsAlg: FunctorK[_root_.com.dwolla.fs2aws.kms.KmsAlg] = functorKForKmsAlg()

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
