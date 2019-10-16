package com.dwolla.fs2aws.s3

import cats.effect._
import cats.implicits._
import com.amazonaws.event.ProgressEvent
import com.amazonaws.event.ProgressEventType._
import com.amazonaws.services.s3.model.{Bucket => _, _}
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.transfer.internal._
import com.dwolla.fs2utils.Pagination
import fs2.{io, _}

import scala.jdk.CollectionConverters._

trait S3Client[F[_]] {
  val blocker: Blocker
  def listBucket(bucket: Bucket): Stream[F, Key]
  def uploadSink(bucket: Bucket, key: Key, objectMetadata: ObjectMetadata): Pipe[F, Byte, Unit]
  def downloadObject(bucket: Bucket, key: Key): Stream[F, Byte]
  def deleteObject(bucket: Bucket, key: Key): Stream[F, Unit]
}

object S3Client {
  def resource[F[_] : ConcurrentEffect : ContextShift]: Resource[F, S3Client[F]] =
    resource[F](None)

  def resource[F[_] : ConcurrentEffect : ContextShift](blocker: Option[Blocker] = None): Resource[F, S3Client[F]] =
    for {
      blocker <- blocker.fold(Blocker[F])(Resource.pure[F, Blocker](_))
      transferManager <- Resource.make(acquireTransferManager[F])(shutdown[F])
    } yield new S3ClientImpl[F](transferManager, blocker)

  def stream[F[_] : ConcurrentEffect : ContextShift]: Stream[F, S3Client[F]] =
    Stream.resource(S3Client.resource[F])

  private def acquireTransferManager[F[_] : Sync]: F[TransferManager] =
    Sync[F].delay(TransferManagerBuilder.defaultTransferManager())

  private def shutdown[F[_] : Sync](tm: TransferManager): F[Unit] =
    Sync[F].delay(tm.shutdownNow())

  class S3ClientImpl[F[_] : ConcurrentEffect : ContextShift] private[s3](transferManager: TransferManager, val blocker: Blocker) extends S3Client[F] {
    override def uploadSink(bucket: Bucket,
                            key: Key,
                            objectMetadata: ObjectMetadata
                           ): Pipe[F, Byte, Unit] = (s: Stream[F, Byte]) =>
      for {
        is <- s.through(io.toInputStream)
        uploadRequest = new PutObjectRequest(bucket, key, is, objectMetadata)
        _ <- upload(uploadRequest)
      } yield ()

    override def downloadObject(bucket: Bucket, key: Key): Stream[F, Byte] =
      io.readInputStream(Sync[F].delay(transferManager.getAmazonS3Client.getObject(bucket, key).getObjectContent), chunkSize = 128, blocker)

    override def deleteObject(bucket: Bucket, key: Key): Stream[F, Unit] =
      Stream.eval(Sync[F].delay(transferManager.getAmazonS3Client.deleteObject(bucket, key)))

    private def upload(req: PutObjectRequest): Stream[F, Unit] =
      Stream.eval(Async[F].async[Unit] { cb =>
        transferManager.upload(req, new S3ProgressListener() {
          private def success(): Unit = cb(Right(()))

          private def failure(ex: Exception): Unit = cb(Left(ex))

          override def onPersistableTransfer(pt: PersistableTransfer): Unit = ()

          override def progressChanged(event: ProgressEvent): Unit =
            event.getEventType match {
              case TRANSFER_COMPLETED_EVENT => success()
              case TRANSFER_FAILED_EVENT => failure(TransferFailedEventException)
              case TRANSFER_CANCELED_EVENT => failure(TransferCanceledEventException)
              case _ => ()
            }
        })

        ()
      })

    override def listBucket(bucket: Bucket): Stream[F, Key] =
      Pagination.offsetUnfoldChunkEval[F, String, Key] { maybeMarker: Option[String] =>
        val request = new ListObjectsRequest().withBucketName(bucket)
        maybeMarker.foreach(request.setMarker)

        for {
          res <- Sync[F].delay(transferManager.getAmazonS3Client.listObjects(request))
        } yield {
          val resultChunk = Chunk.seq(res.getObjectSummaries.asScala).map(_.getKey).map(tagKey)
          val maybeNextMarker = Option(res.getNextMarker)

          (resultChunk, maybeNextMarker)
        }
      }
  }

  case object TransferFailedEventException extends RuntimeException("S3 transfer failed")
  case object TransferCanceledEventException extends RuntimeException("S3 transfer canceled")
}
