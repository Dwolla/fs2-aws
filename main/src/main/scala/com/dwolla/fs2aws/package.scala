package com.dwolla

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.language.higherKinds

package object fs2aws {
  //noinspection ScalaUnusedSymbol
  type PaginatedRequest = AmazonWebServiceRequest {def setNextToken(s: String): Unit}
  //noinspection AccessorLikeMethodIsEmptyParen,ScalaUnusedSymbol
  type PaginatedResult = {def getNextToken(): String}
  type AwsAsyncFunction[Req <: AmazonWebServiceRequest, Res] = (Req, AsyncHandler[Req, Res]) => JFuture[Res]

  implicit class ExecuteViaOps[Req <: AmazonWebServiceRequest](val req: Req) extends AnyVal {
    def executeVia[F[_]] = new ExecuteVia[F, Req](req)
  }

  implicit class FetchAllOps[Req <: PaginatedRequest](val requestFactory: () => Req) {
    def fetchAll[F[_]] = new FetchAll[F, Req](requestFactory)
  }

}
