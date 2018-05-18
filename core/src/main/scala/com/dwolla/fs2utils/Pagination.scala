package com.dwolla.fs2utils

import cats._
import cats.data._
import cats.implicits._
import fs2._

import scala.language.higherKinds

object Pagination {
  private sealed trait PageIndicator[S]
  private case class FirstPage[S]() extends PageIndicator[S]
  private case class NextPage[S](token: S) extends PageIndicator[S]
  private case class NoMorePages[S]() extends PageIndicator[S]

  def offsetUnfoldSegmentEval[F[_], S, O](f: Option[S] ⇒ F[(Segment[O, Unit], Option[S])])
                                         (implicit F: Applicative[F]): Stream[F, O] = {
    def fetchPage(maybeNextPageToken: Option[S]): F[Option[(Segment[O, Unit], PageIndicator[S])]] = {
      f(maybeNextPageToken).map {
        case (segment, Some(nextToken)) ⇒ Option((segment, NextPage(nextToken)))
        case (segment, None) ⇒ Option((segment, NoMorePages[S]()))
      }
    }

    Stream.unfoldSegmentEval[F, PageIndicator[S], O](FirstPage[S]()) {
      case FirstPage() ⇒ fetchPage(None)
      case NextPage(token) ⇒ fetchPage(Some(token))
      case NoMorePages() ⇒ F.pure(None)
    }
  }

  def offsetUnfoldEval[F[_] : Applicative, S, O](f: Option[S] ⇒ F[(O, Option[S])]): Stream[F, O] =
    offsetUnfoldSegmentEval(Kleisli(f).map(tuple ⇒ (Segment(tuple._1), tuple._2)).run)
}
