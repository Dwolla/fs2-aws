package com.dwolla.fs2aws.s3

import cats.effect._
import com.amazonaws.event.ProgressEvent
import com.amazonaws.event.ProgressEventType._
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.transfer.internal._
import fs2.{io, _}

import scala.concurrent.ExecutionContext

trait S3Client[F[_]] {
  def uploadSink(bucket: Bucket, key: Key, objectMetadata: ObjectMetadata): Sink[F, Byte]
}

object S3Client {
  def stream[F[_] : Effect](implicit ec: ExecutionContext): Stream[F, S3Client[F]] =
    Stream.bracket(Sync[F].delay(TransferManagerBuilder.defaultTransferManager()))(tm ⇒ Stream.emit(new S3ClientImpl[F](tm)), tm ⇒ Sync[F].delay(tm.shutdownNow()))

  class S3ClientImpl[F[_] : Effect] private[s3](transferManager: TransferManager)
                                               (implicit ec: ExecutionContext) extends S3Client[F] {
    override def uploadSink(bucket: Bucket,
                            key: Key,
                            objectMetadata: ObjectMetadata
                           ): Sink[F, Byte] = (s: Stream[F, Byte]) ⇒
      for {
        is ← s.through(io.toInputStream)
        uploadRequest = new PutObjectRequest(bucket, key, is, objectMetadata)
        _ ← upload(uploadRequest)
      } yield ()

    private def upload(req: PutObjectRequest): Stream[F, Unit] =
      Stream.eval(Async[F].async[Unit] { cb ⇒
        transferManager.upload(req, new S3ProgressListener() {
          private def success(): Unit = cb(Right(()))
          private def failure(ex: Exception): Unit = cb(Left(ex))

          override def onPersistableTransfer(pt: PersistableTransfer): Unit = ()

          override def progressChanged(event: ProgressEvent): Unit =
            event.getEventType match {
              case TRANSFER_COMPLETED_EVENT ⇒ success()
              case TRANSFER_FAILED_EVENT ⇒ failure(TransferFailedEventException)
              case TRANSFER_CANCELED_EVENT ⇒ failure(TransferCanceledEventException)
              case _ ⇒ ()
            }
        })

        ()
      })
  }

  case object TransferFailedEventException extends RuntimeException("S3 transfer failed")
  case object TransferCanceledEventException extends RuntimeException("S3 transfer canceled")
}
