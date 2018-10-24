package com.dwolla.fs2aws.s3

import java.io.ByteArrayInputStream

import cats.effect._
import cats.effect.concurrent.Deferred
import cats.implicits._
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.AmazonClientException
import com.amazonaws.event.{ProgressEvent, ProgressListener, _}
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener
import com.amazonaws.services.s3.transfer.model.UploadResult
import com.dwolla.fs2aws.s3.S3Client.S3ClientImpl
import fs2._
import org.apache.http.client.methods.HttpRequestBase
import org.mockito.Mockito._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute._
import org.specs2.matcher.{MatchResult, Matchers}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.matcher.IOMatchers

class S3ClientSpec(implicit ee: ExecutionEnv) extends Specification with Matchers with Mockito with IOMatchers {

  implicit val cs = IO.contextShift(ee.executionContext)

  val tm = mock[TransferManager]
  val mockS3 = mock[AmazonS3]
  when(tm.getAmazonS3Client).thenReturn(mockS3)
  val client = new S3ClientImpl[IO](tm)
  val bucket = tagBucket("bucket")
  val key = tagKey("key")
  val expectedContentType = "text/x-shellscript"

  val mockTransferManager = IO(mock[TransferManager])
  val objectMetadata = IO {
    val om = new ObjectMetadata()
    om.setContentType(expectedContentType)
    om
  }

  "S3Client" should {
    "upload the bytes of the stream to the given S3 location" >> {
      val expectedBytes = (0 until 100).map(_.toByte)
      val expectedContentType = "text/x-shellscript"
      val mockTransferManager = IO(mock[TransferManager])

      val objectMetadata = IO {
        val om = new ObjectMetadata()
        om.setContentType(expectedContentType)
        om
      }
      for {
        deferredUploadArguments <- Deferred[IO, (PutObjectRequest, S3ProgressListener)]
        tm ← mockTransferManager.flatTap(tm ⇒ IO.asyncF[Unit] { mockSetupComplete =>
          for {
            arguments <- IO.async[(PutObjectRequest, S3ProgressListener)] { captureArguments =>
              tm.upload(any[PutObjectRequest], any[S3ProgressListener]) answers ((arr: Array[AnyRef]) =>
                arr.toList match {
                  case (por: PutObjectRequest) :: (s3ProgressListener: S3ProgressListener) :: Nil =>
                    captureArguments(Right((por, s3ProgressListener)))
                    new NoOpUpload
                  case _ =>
                    captureArguments(Left(new RuntimeException("the wrong type of arguments were passed to the function")))
                    null
                })

              mockSetupComplete(Right(()))
            }
            _ <- deferredUploadArguments.complete(arguments)
          } yield ()
        })
        om ← objectMetadata
        client: S3Client[IO] = new S3ClientImpl[IO](tm)

        fiber ← Concurrent[IO].start(Stream.emits(expectedBytes).covary[IO].to(client.uploadSink(bucket, key, om)).compile.drain)

        (putObjectRequest, s3ProgressListener) <- deferredUploadArguments.get
        passedBytes ← io.readInputStream(IO(putObjectRequest.getInputStream), 16, ee.ec).compile.toList

        _ ← IO(s3ProgressListener.progressChanged(new ProgressEvent(ProgressEventType.TRANSFER_COMPLETED_EVENT)))
        _ ← fiber.join
      } yield {
        putObjectRequest.getBucketName must_== "bucket"
        putObjectRequest.getKey must_== "key"
        passedBytes must_== expectedBytes
        putObjectRequest.getMetadata.getContentType must_== expectedContentType
      }
    }

    "download the bytes of the stream from the given S3 location" >> {
      val obj = mock[S3Object]
      val expected = (0 until 100).toArray.map(_.toByte)
      when(mockS3.getObject(bucket, key)).thenReturn(obj)
      when(obj.getObjectContent).thenReturn(new S3ObjectInputStream(new ByteArrayInputStream(expected), mock[HttpRequestBase]))

      client.downloadObject(bucket, key, ee.ec).compile.toList must returnValue(equalTo(expected.toList))
    }

    "delete object from the given S3 location" >> {
      client.deleteObject(bucket, key).compile.drain must returnOk

      there was one(mockS3).deleteObject(bucket, key)
    }
  }

  private implicit def ioToResult[T](io: IO[MatchResult[T]]): Result = io.unsafeToFuture().await
}

class NoOpUpload extends Upload {
  override def waitForUploadResult(): UploadResult = ???
  override def pause(): PersistableUpload = ???
  override def tryPause(forceCancelTransfers: Boolean): PauseResult[PersistableUpload] = ???
  override def abort(): Unit = ???
  override def isDone: Boolean = ???
  override def waitForCompletion(): Unit = ???
  override def waitForException(): AmazonClientException = ???
  override def getDescription: String = ???
  override def getState: Transfer.TransferState = ???
  override def addProgressListener(listener: ProgressListener): Unit = ???
  override def removeProgressListener(listener: ProgressListener): Unit = ???
  override def getProgress: TransferProgress = ???
  @deprecated("upstream deprecation", "upstream") override def addProgressListener(listener: com.amazonaws.services.s3.model.ProgressListener): Unit = ???
  @deprecated("upstream deprecation", "upstream") override def removeProgressListener(listener: com.amazonaws.services.s3.model.ProgressListener): Unit = ???
}
