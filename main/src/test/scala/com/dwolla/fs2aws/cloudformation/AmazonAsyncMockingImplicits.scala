package com.dwolla.fs2aws.cloudformation

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock

import scala.reflect.ClassTag

object AmazonAsyncMockingImplicits {

  /**
   * For example:
   *
   * {{{
   *   import com.dwolla.awssdk.AmazonMockAsyncImplicits._
   *   import com.amazonaws.services.cloudformation.model.{UpdateStackRequest, UpdateStackResult}
   *
   *   mockedMethod(mock[AmazonCloudFormationAsync].updateStackAsync) answers(
   *     new UpdateStackRequest() → new UpdateStackResult(),
   *     new UpdateStackRequest().withStackName("bad-name") → new RuntimeException("bad stack name!")
   *   )
   * }}}
   *
   * @param func the Amazon async client method to be mocked
   * @tparam Req an Amazon web service request object
   * @tparam Res an Amazon web service result object
   * @return MockAnswerMappingBuilder
   */
  def mockedMethod[Req <: AmazonWebServiceRequest : ClassTag, Res](func: (Req, AsyncHandler[Req, Res]) => JFuture[Res]): MockAnswerMappingBuilder[Req, Res] = new MockAnswerMappingBuilder(func)

  /**
   * For example:
   *
   * {{{
   *   import com.dwolla.awssdk.AmazonMockAsyncImplicits._
   *   new UpdateStackResult() completes mock[AmazonCloudFormationAsync].updateStackAsync
   * }}}
   *
   */
  implicit class AmazonAsyncResult[Res](res: Res) {

    def completes[Req <: AmazonWebServiceRequest : ClassTag](func: (Req, AsyncHandler[Req, Res]) => JFuture[Res]): Unit = {

      when(func(any[Req], any[AsyncHandler[Req, Res]])) thenAnswer ((invocation: InvocationOnMock) => {
        invocation.getArguments match {
          case Array(req, handler) => handler.asInstanceOf[AsyncHandler[Req, Res]].onSuccess(req.asInstanceOf[Req], res)
        }
        null
      })

      ()
    }
  }

  /**
   * For example:
   *
   * {{{
   *   import com.dwolla.awssdk.AmazonMockAsyncImplicits._
   *   import com.amazonaws.services.ecs.AmazonECSAsync
   *   import com.amazonaws.services.ecs.model.{Cluster, DescribeClustersResult, ListContainerInstancesRequest, ListContainerInstancesResult}
   *
   *   Map(
   *     new ListContainerInstancesRequest().withCluster("cluster1") → new ListContainerInstancesResult().withContainerInstanceArns("arn1").withNextToken("next-token"),
   *     new ListContainerInstancesRequest().withCluster("cluster1").withNextToken("next-token") → new ListContainerInstancesResult().withContainerInstanceArns("arn2")
   *   ) completes mock[AmazonECSAsync].listContainerInstancesAsync
   * }}}
   */
  implicit class AmazonAsyncResults[Req <: AmazonWebServiceRequest : ClassTag, Res](responseMapping: Map[Req, Either[Exception, Res]]) {

    def completes(func: (Req, AsyncHandler[Req, Res]) => JFuture[Res]): Unit = {
      when(func(any[Req], any[AsyncHandler[Req, Res]])) thenAnswer ((invocation: InvocationOnMock) => {
        invocation.getArguments match {
          case Array(req: Req, _) if !responseMapping.contains(req) => throw TestSetupException(s"req/res mapping does not contain `$req`. Look at the mock setup: is an expected case missing?")
          case Array(req: Req, handler: AsyncHandler[Req, Res]) => responseMapping(req) match {
            case Left(ex) => handler.onError(ex)
            case Right(res) => handler.onSuccess(req, res)
          }
        }
        null
      })

      ()
    }

    case class TestSetupException(msg: String) extends RuntimeException(msg)
  }

  class MockAnswerMappingBuilder[Req <: AmazonWebServiceRequest : ClassTag, Res](func: (Req, AsyncHandler[Req, Res]) => JFuture[Res]) {
    def answers(requestResponseMappings: (Req, Either[Exception, Res])*): Unit = Map(requestResponseMappings: _*) completes func
  }

  implicit def liftExceptionToLeft[Res](ex: Exception): Either[Exception, Res] = Left(ex)
  implicit def liftResponseToRight[Res](res: Res): Either[Exception, Res] = Right(res)
}