package com.dwolla.fs2aws.cloudformation

import cats.effect._
import cats.implicits._
import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudformation.model.Capability.CAPABILITY_IAM
import com.amazonaws.services.cloudformation.model.ChangeSetType._
import com.amazonaws.services.cloudformation.model.StackStatus._
import com.amazonaws.services.cloudformation.model.{Parameter => AwsParameter, _}
import com.amazonaws.services.cloudformation._
import com.dwolla.fs2aws._
import com.dwolla.fs2aws.cloudformation.CloudFormationClient._
import com.dwolla.fs2aws.cloudformation.Implicits._

import scala.jdk.CollectionConverters._
import scala.language.reflectiveCalls

trait CloudFormationClient[F[_]] {
  def createOrUpdateTemplate(stackName: String,
                             template: String,
                             params: List[(String, String)] = List.empty[(String, String)],
                             roleArn: Option[String] = None,
                             changeSetName: Option[String] = None): F[StackID]
}

class CloudFormationClientImpl[F[_] : Effect](client: AmazonCloudFormationAsync) extends CloudFormationClient[F] {
  override def createOrUpdateTemplate(stackName: String,
                                      template: String,
                                      params: List[(String, String)],
                                      roleArn: Option[String],
                                      changeSetName: Option[String]): F[StackID] =
    for {
      maybeStack <- getStackByName(stackName)
      stackOperation = maybeStack.fold(buildStackOperation(createStack, CREATE)) {
        case stack if updatableStackStatuses.contains(stackStatus(stack.getStackStatus)) => buildStackOperation(updateStack, UPDATE)
        case stack => (_, _) => Sync[F].raiseError(StackNotUpdatableException(stack.getStackName, stack.getStackStatus))
      }
      res <- stackOperation(StackDetails(stackName, template, params, roleArn), changeSetName)
    } yield res

  private def buildStackOperation[T](func: T => F[StackID], changeSetType: ChangeSetType)
                                    (implicit ev1: StackDetails => T): (StackDetails, Option[String]) => F[StackID] =
    (stackDetails, changeSetName) => changeSetName.fold(func(stackDetails))(createChangeSet(_, stackDetails.withChangeSetType(changeSetType)))

  private def getStackByName(name: String): F[Option[Stack]] = (() => new DescribeStacksRequest()).fetchAll(client.describeStacksAsync)(_.getStacks.asScala.toSeq)
    .filter(s => s.getStackName == name && StackStatus.valueOf(s.getStackStatus) != DELETE_COMPLETE)
    .compile
    .last

  private def createStack(createStackRequest: CreateStackRequest): F[StackID] = makeRequestAndExtractStackId(createStackRequest, client.createStackAsync)

  private def updateStack(updateStackRequest: UpdateStackRequest): F[StackID] = makeRequestAndExtractStackId(updateStackRequest, client.updateStackAsync)

  private def createChangeSet(changeSetName: String, createChangeSetRequest: CreateChangeSetRequest): F[StackID] =
    makeRequestAndExtractStackId(createChangeSetRequest.withChangeSetName(changeSetName), client.createChangeSetAsync)

  //noinspection AccessorLikeMethodIsEmptyParen
  private def makeRequestAndExtractStackId[Req <: AmazonWebServiceRequest, Res <: {def getStackId() : StackID}](req: Req, func: AwsAsyncFunction[Req, Res]): F[StackID] =
    req.executeVia(func).map(_.getStackId())

}

object CloudFormationClient {
  def apply[F[_] : Effect]: CloudFormationClient[F] = new CloudFormationClientImpl[F](clientForRegion(None))

  def apply[F[_] : Effect](r: String): CloudFormationClient[F] = apply[F](Regions.fromName(r))

  def apply[F[_] : Effect](r: Regions): CloudFormationClient[F] = new CloudFormationClientImpl[F](clientForRegion(Option(r)))

  val updatableStackStatuses: Seq[StackStatus] = Seq(
    CREATE_COMPLETE,
    ROLLBACK_COMPLETE,
    UPDATE_COMPLETE,
    UPDATE_ROLLBACK_COMPLETE
  )

  private def clientForRegion(r: Option[Regions]) = r.fold(AmazonCloudFormationAsyncClientBuilder.defaultClient()) { providedRegion =>
    AmazonCloudFormationAsyncClientBuilder.standard().withRegion(providedRegion).build()
  }
}

case class StackDetails(name: String, template: String, parameters: List[AwsParameter], roleArn: Option[String] = None)

trait Builder[StackRequest] {
  def withStackName(name: String): StackRequest
  def withTemplateBody(name: String): StackRequest
  def withParameters(params: List[AwsParameter]): StackRequest
  def withCapabilities(capabilities: Capability*): StackRequest
  def withRoleArn(roleArn: String): StackRequest
}

object Implicits {
  implicit class CreateStackRequestToBuilder(s: CreateStackRequest) extends Builder[CreateStackRequest] {
    override def withStackName(name: String): CreateStackRequest = s.withStackName(name)
    override def withTemplateBody(name: String): CreateStackRequest = s.withTemplateBody(name)
    override def withParameters(params: List[AwsParameter]): CreateStackRequest = s.withParameters(params.asJavaCollection)
    override def withCapabilities(capabilities: Capability*): CreateStackRequest = s.withCapabilities(capabilities: _*)
    override def withRoleArn(roleArn: String): CreateStackRequest = s.withRoleARN(roleArn)
  }
  implicit class CreateChangeSetRequestToBuilder(s: CreateChangeSetRequest) extends Builder[CreateChangeSetRequest] {
    override def withStackName(name: String): CreateChangeSetRequest = s.withStackName(name)
    override def withTemplateBody(name: String): CreateChangeSetRequest = s.withTemplateBody(name)
    override def withParameters(params: List[AwsParameter]): CreateChangeSetRequest = s.withParameters(params.asJavaCollection)
    override def withCapabilities(capabilities: Capability*): CreateChangeSetRequest = s.withCapabilities(capabilities: _*)
    override def withRoleArn(roleArn: String): CreateChangeSetRequest = s.withRoleARN(roleArn)
  }
  implicit class UpdateStackRequestToBuilder(s: UpdateStackRequest) extends Builder[UpdateStackRequest] {
    override def withStackName(name: String): UpdateStackRequest = s.withStackName(name)
    override def withTemplateBody(name: String): UpdateStackRequest = s.withTemplateBody(name)
    override def withParameters(params: List[AwsParameter]): UpdateStackRequest = s.withParameters(params.asJavaCollection)
    override def withCapabilities(capabilities: Capability*): UpdateStackRequest = s.withCapabilities(capabilities: _*)
    override def withRoleArn(roleArn: String): UpdateStackRequest = s.withRoleARN(roleArn)
  }

  implicit def potentialStackToCreateRequest(ps: StackDetails): CreateStackRequest = populate(ps, new CreateStackRequest)
  implicit def potentialStackToUpdateRequest(ps: StackDetails): UpdateStackRequest = populate(ps, new UpdateStackRequest)
  implicit def potentialStackToCreateChangeSetRequest(ps: StackDetails): CreateChangeSetRequest = populate(ps, new CreateChangeSetRequest)
  implicit def tuplesToParams(tuples: List[(String, String)]): List[AwsParameter] = tuples.map {
    case (key, value) => new AwsParameter().withParameterKey(key).withParameterValue(value)
  }
  implicit def stackStatus(status: String): StackStatus = StackStatus.valueOf(status)

  private def populate[T](ps: StackDetails, builder: Builder[T])(implicit ev: T => Builder[T]): T = {
    val t = builder.withStackName(ps.name)
      .withTemplateBody(ps.template)
      .withParameters(ps.parameters)
      .withCapabilities(CAPABILITY_IAM)

    ps.roleArn.fold(t)(t.withRoleArn)
  }
}
