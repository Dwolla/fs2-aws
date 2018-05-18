package com.dwolla.fs2aws
package examples

import cats.effect._
import com.amazonaws.services.cloudformation._
import com.amazonaws.services.cloudformation.model._

class ExecuteViaTest {

  val req = new DescribeStackEventsRequest()

  val client: AmazonCloudFormationAsync = new FakeAmazonCloudFormationAsyncClient

  val x: IO[DescribeStackEventsResult] = req.executeVia[IO](client.describeStackEventsAsync)

}
