package com.dwolla.fs2aws.s3

import java.io.ByteArrayInputStream

import cats.effect._
import cats.implicits._
import com.amazonaws.event.{ProgressEvent, _}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener
import com.dwolla.fs2aws.s3.S3Client.S3ClientImpl
import fs2._
import org.apache.http.client.methods.HttpRequestBase
import org.mockito.Mockito._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute._
import org.specs2.matcher.{MatchResult, Matchers}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class S3ClientSpec(implicit ee: ExecutionEnv) extends Specification with Matchers with Mockito {

  val tm = mock[TransferManager]
  val mockS3 = mock[AmazonS3]
  when(tm.getAmazonS3Client).thenReturn(mockS3)
  val client = new S3ClientImpl[IO](tm)
  val bucket = tagBucket("bucket")
  val key = tagKey("key")

  "S3Client" should {
    "upload the bytes of the stream to the given S3 location" >> {
      val expectedBytes = (0 until 100).map(_.toByte)
      val expectedContentType = "text/x-shellscript"
      val mockTransferManager = IO(mock[TransferManager])
      val capturedPutObjectRequest = IO(capture[PutObjectRequest])
      val capturedS3ProgressListener = IO(capture[S3ProgressListener])

      val objectMetadata = IO {
        val om = new ObjectMetadata()
        om.setContentType(expectedContentType)
        om
      }
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

    "download the bytes of the stream from the given S3 location" >> {
      val obj = mock[S3Object]
      val expected = (0 until 100).toArray.map(_.toByte)
      when(mockS3.getObject(bucket, key)).thenReturn(obj)
      when(obj.getObjectContent).thenReturn(new S3ObjectInputStream(new ByteArrayInputStream(expected), mock[HttpRequestBase]))

      client.downloadObject(bucket, key).compile.toList.unsafeRunSync must_== expected.toList
    }

    "delete object from the given S3 location" >> {
      client.deleteObject(bucket, key).compile.drain.unsafeRunSync

      there was one(mockS3).deleteObject(bucket, key)
    }
  }

  private implicit def ioToResult[T](io: IO[MatchResult[T]]): Result = io.unsafeToFuture().await
}
