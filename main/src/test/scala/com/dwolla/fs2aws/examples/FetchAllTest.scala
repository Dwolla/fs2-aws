package com.dwolla.fs2aws
package examples

import cats.effect._
import com.amazonaws.services.cloudformation.AmazonCloudFormationAsync
import com.amazonaws.services.cloudformation.model._
import fs2._

import scala.jdk.CollectionConverters._

class FetchAllTest {

  val requestFactory = () => new DescribeStackEventsRequest()

  val client: AmazonCloudFormationAsync = new FakeAmazonCloudFormationAsyncClient

  val x: Stream[IO, StackEvent] = requestFactory.fetchAll[IO](client.describeStackEventsAsync)(_.getStackEvents.asScala.toSeq)

}
