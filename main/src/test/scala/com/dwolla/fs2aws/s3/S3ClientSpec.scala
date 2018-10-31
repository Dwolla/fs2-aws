package com.dwolla.fs2aws.s3

import cats.effect._
import cats.implicits._
import com.amazonaws.event.{ProgressEvent, _}
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener
import com.dwolla.fs2aws.s3.S3Client.S3ClientImpl
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.{MatchResult, Matchers}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import fs2._
import org.specs2.execute._

class S3ClientSpec(implicit ee: ExecutionEnv) extends Specification with Matchers with Mockito {

  val expectedBytes = (0 until 100).map(_.toByte)
  val bucket = tagBucket("bucket")
  val key = tagKey("key")
  val expectedContentType = "text/x-shellscript"

  val capturedPutObjectRequest = IO(capture[PutObjectRequest])
  val capturedS3ProgressListener = IO(capture[S3ProgressListener])
  val mockTransferManager = IO(mock[TransferManager])
  val objectMetadata = IO {
    val om = new ObjectMetadata()
    om.setContentType(expectedContentType)
    om
  }

  "S3Client" should {
    "upload the bytes of the stream to the given S3 location" >> {
      for {
        putCaptor ← capturedPutObjectRequest
        progressListenerCaptor ← capturedS3ProgressListener
        tm ← mockTransferManager.flatTap(tm ⇒ IO {
          tm.upload(putCaptor.capture, progressListenerCaptor.capture) returns null
        })
        om ← objectMetadata
        client: S3Client[IO] = new S3ClientImpl[IO](tm)

        fiber ← Concurrent[IO].start(Stream.emits(expectedBytes).covary[IO].to(client.uploadSink(bucket, key, om)).compile.drain)

        capturedObjectMetadata ← IO(putCaptor.value.getMetadata)
        passedBytes ← io.readInputStream(IO(putCaptor.value.getInputStream), 16).compile.toList

        _ ← IO(progressListenerCaptor.value.progressChanged(new ProgressEvent(ProgressEventType.TRANSFER_COMPLETED_EVENT)))
        _ ← fiber.join
      } yield {
        putCaptor.value.getBucketName must_== "bucket"
        putCaptor.value.getKey must_== "key"
        passedBytes must_== expectedBytes
        capturedObjectMetadata.getContentType must_== expectedContentType
      }
    }
  }

  private implicit def ioToResult[T](io: IO[MatchResult[T]]): Result = io.unsafeToFuture().await
}
