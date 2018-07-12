package com.dwolla.fs2aws

import cats.effect._
import com.dwolla.fs2aws.kms._
import org.mockito.ArgumentMatchers
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class KmsDecrypterMockabilityTest(implicit ee: ExecutionEnv) extends Specification with Mockito {

  trait Setup extends Scope {
    val mockKmsDecrypter = mock[KmsDecrypter[IO]]

    mockKmsDecrypter.decrypt(
      ArgumentMatchers.any[Transform[String]],
      ArgumentMatchers.any[String]
    ) returns IO("hello world").map(_.getBytes("UTF-8"))
  }

  "KmsDecrypter" should {
    "be mockable when constructed directly" in new Setup {
      val output = mockKmsDecrypter.decrypt[String](base64DecodingTransform, "crypto-test")
        .map(new String(_, "UTF-8"))
        .unsafeToFuture()

      output should be_==("hello world").await

    }
  }

}
