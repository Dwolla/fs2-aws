package com.dwolla.fs2aws.cloudformation

import cats.effect._
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.cloudformation.AmazonCloudFormationAsync
import com.amazonaws.services.cloudformation.model.StackStatus.UPDATE_COMPLETE
import com.amazonaws.services.cloudformation.model._
import com.dwolla.fs2aws.cloudformation.AmazonAsyncMockingImplicits._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

//noinspection RedundantDefaultArgument
class CloudFormationClientSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  trait Setup extends Scope {
    val mockAsyncClient = mock[AmazonCloudFormationAsync]
    val client = new CloudFormationClientImpl[IO](mockAsyncClient)
  }

  trait StackExistsSetup extends Setup {
    new UpdateStackResult().withStackId("updated-stack-id") completes mockAsyncClient.updateStackAsync
    new DescribeStacksResult().withStacks(new Stack().withStackId("stack-id").withStackName("stack-name").withStackStatus(UPDATE_COMPLETE)) completes mockAsyncClient.describeStacksAsync
    new CreateChangeSetResult().withStackId("stack-id-with-change-set") completes mockAsyncClient.createChangeSetAsync
  }

  trait StackExistsOnSecondPageSetup extends Setup {
    new UpdateStackResult().withStackId("updated-stack-id") completes mockAsyncClient.updateStackAsync

    Map(
      new DescribeStacksRequest() -> Right(new DescribeStacksResult()
        .withNextToken("next-token")
        .withStacks(new Stack().withStackId("diff-stack-id").withStackName("diff-stack-name").withStackStatus(UPDATE_COMPLETE))
      ),
      new DescribeStacksRequest().withNextToken("next-token") -> Right(new DescribeStacksResult()
        .withStacks(new Stack().withStackId("stack-id").withStackName("stack-name").withStackStatus(UPDATE_COMPLETE))
      )
    ) completes mockAsyncClient.describeStacksAsync
    new CreateChangeSetResult().withStackId("stack-id-with-change-set") completes mockAsyncClient.createChangeSetAsync
  }

  trait StackMissingSetup extends Setup {
    new DescribeStacksResult() completes mockAsyncClient.describeStacksAsync
    new CreateStackResult().withStackId("created-stack-id") completes mockAsyncClient.createStackAsync
    new CreateChangeSetResult().withStackId("created-stack-id-with-change-set") completes mockAsyncClient.createChangeSetAsync
  }

  "createOrUpdateTemplate" should {
    "call update stack when the stack exists and no changeset name is specified" in new StackExistsSetup {
      val output = client.createOrUpdateTemplate("stack-name", "{}", changeSetName = None)

      output.unsafeToFuture() must be_==("updated-stack-id").await

      there was one(mockAsyncClient).updateStackAsync(any[UpdateStackRequest], any[AsyncHandler[UpdateStackRequest, UpdateStackResult]])
      there was no(mockAsyncClient).createChangeSetAsync(any[CreateChangeSetRequest], any[AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]])
      there was no(mockAsyncClient).createStackAsync(any[CreateStackRequest], any[AsyncHandler[CreateStackRequest, CreateStackResult]])
    }

    "call update stack when the stack exists (but not on the first page of results) and no changeset name is specified" in new StackExistsOnSecondPageSetup {
      val output = client.createOrUpdateTemplate("stack-name", "{}", changeSetName = None)

      output.unsafeToFuture() must be_==("updated-stack-id").await

      there was one(mockAsyncClient).updateStackAsync(any[UpdateStackRequest], any[AsyncHandler[UpdateStackRequest, UpdateStackResult]])
      there was no(mockAsyncClient).createChangeSetAsync(any[CreateChangeSetRequest], any[AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]])
      there was no(mockAsyncClient).createStackAsync(any[CreateStackRequest], any[AsyncHandler[CreateStackRequest, CreateStackResult]])
    }

    "create change set when the stack exists and a changeset name is specified" in new StackExistsSetup {
      val output = client.createOrUpdateTemplate("stack-name", "{}", changeSetName = Option("change-set-name"))

      output.unsafeToFuture() must be_==("stack-id-with-change-set").await

      val requestCaptor = capture[CreateChangeSetRequest]

      there was one(mockAsyncClient).createChangeSetAsync(requestCaptor.capture, any[AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]])
      there was no(mockAsyncClient).updateStackAsync(any[UpdateStackRequest], any[AsyncHandler[UpdateStackRequest, UpdateStackResult]])
      there was no(mockAsyncClient).createStackAsync(any[CreateStackRequest], any[AsyncHandler[CreateStackRequest, CreateStackResult]])

      requestCaptor.value must beLike { case req =>
        req.getChangeSetName must_== "change-set-name"
        req.getChangeSetType must_== "UPDATE"
      }
    }

    "create stack when it does not exist and no changeset name is specified" in new StackMissingSetup {
      val output = client.createOrUpdateTemplate("stack-name", "{}")

      output.unsafeToFuture() must be_==("created-stack-id").await

      there was one(mockAsyncClient).createStackAsync(any[CreateStackRequest], any[AsyncHandler[CreateStackRequest, CreateStackResult]])
      there was no(mockAsyncClient).createChangeSetAsync(any[CreateChangeSetRequest], any[AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]])
      there was no(mockAsyncClient).updateStackAsync(any[UpdateStackRequest], any[AsyncHandler[UpdateStackRequest, UpdateStackResult]])
    }

    "create change set when stack does not exist and a changeset name is specified" in new StackMissingSetup {
      val output = client.createOrUpdateTemplate("stack-name", "{}", changeSetName = Option("change-set-name"))

      output.unsafeToFuture() must be_==("created-stack-id-with-change-set").await

      val requestCaptor = capture[CreateChangeSetRequest]

      there was one(mockAsyncClient).createChangeSetAsync(requestCaptor.capture, any[AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]])
      there was no(mockAsyncClient).createStackAsync(any[CreateStackRequest], any[AsyncHandler[CreateStackRequest, CreateStackResult]])
      there was no(mockAsyncClient).updateStackAsync(any[UpdateStackRequest], any[AsyncHandler[UpdateStackRequest, UpdateStackResult]])

      requestCaptor.value must beLike { case req =>
        req.getChangeSetName must_== "change-set-name"
        req.getChangeSetType must_== "CREATE"
      }
    }
  }
}
