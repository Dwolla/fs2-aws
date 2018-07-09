package com.dwolla.fs2aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.Region
import com.amazonaws.services.cloudformation.AmazonCloudFormationAsync
import com.amazonaws.services.cloudformation.model._

class FakeAmazonCloudFormationAsyncClient extends AmazonCloudFormationAsync {
  override def cancelUpdateStackAsync(cancelUpdateStackRequest: CancelUpdateStackRequest) = ???

  override def cancelUpdateStackAsync(cancelUpdateStackRequest: CancelUpdateStackRequest, asyncHandler: AsyncHandler[CancelUpdateStackRequest, CancelUpdateStackResult]) = ???

  override def continueUpdateRollbackAsync(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest) = ???

  override def continueUpdateRollbackAsync(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest,
                                           asyncHandler: AsyncHandler[ContinueUpdateRollbackRequest, ContinueUpdateRollbackResult]) = ???

  override def createChangeSetAsync(createChangeSetRequest: CreateChangeSetRequest) = ???

  override def createChangeSetAsync(createChangeSetRequest: CreateChangeSetRequest, asyncHandler: AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]) = ???

  override def createStackAsync(createStackRequest: CreateStackRequest) = ???

  override def createStackAsync(createStackRequest: CreateStackRequest, asyncHandler: AsyncHandler[CreateStackRequest, CreateStackResult]) = ???

  override def createStackInstancesAsync(createStackInstancesRequest: CreateStackInstancesRequest) = ???

  override def createStackInstancesAsync(createStackInstancesRequest: CreateStackInstancesRequest,
                                         asyncHandler: AsyncHandler[CreateStackInstancesRequest, CreateStackInstancesResult]) = ???

  override def createStackSetAsync(createStackSetRequest: CreateStackSetRequest) = ???

  override def createStackSetAsync(createStackSetRequest: CreateStackSetRequest, asyncHandler: AsyncHandler[CreateStackSetRequest, CreateStackSetResult]) = ???

  override def deleteChangeSetAsync(deleteChangeSetRequest: DeleteChangeSetRequest) = ???

  override def deleteChangeSetAsync(deleteChangeSetRequest: DeleteChangeSetRequest, asyncHandler: AsyncHandler[DeleteChangeSetRequest, DeleteChangeSetResult]) = ???

  override def deleteStackAsync(deleteStackRequest: DeleteStackRequest) = ???

  override def deleteStackAsync(deleteStackRequest: DeleteStackRequest, asyncHandler: AsyncHandler[DeleteStackRequest, DeleteStackResult]) = ???

  override def deleteStackInstancesAsync(deleteStackInstancesRequest: DeleteStackInstancesRequest) = ???

  override def deleteStackInstancesAsync(deleteStackInstancesRequest: DeleteStackInstancesRequest,
                                         asyncHandler: AsyncHandler[DeleteStackInstancesRequest, DeleteStackInstancesResult]) = ???

  override def deleteStackSetAsync(deleteStackSetRequest: DeleteStackSetRequest) = ???

  override def deleteStackSetAsync(deleteStackSetRequest: DeleteStackSetRequest, asyncHandler: AsyncHandler[DeleteStackSetRequest, DeleteStackSetResult]) = ???

  override def describeAccountLimitsAsync(describeAccountLimitsRequest: DescribeAccountLimitsRequest) = ???

  override def describeAccountLimitsAsync(describeAccountLimitsRequest: DescribeAccountLimitsRequest,
                                          asyncHandler: AsyncHandler[DescribeAccountLimitsRequest, DescribeAccountLimitsResult]) = ???

  override def describeChangeSetAsync(describeChangeSetRequest: DescribeChangeSetRequest) = ???

  override def describeChangeSetAsync(describeChangeSetRequest: DescribeChangeSetRequest, asyncHandler: AsyncHandler[DescribeChangeSetRequest, DescribeChangeSetResult]) = ???

  override def describeStackEventsAsync(describeStackEventsRequest: DescribeStackEventsRequest) = ???

  override def describeStackEventsAsync(describeStackEventsRequest: DescribeStackEventsRequest,
                                        asyncHandler: AsyncHandler[DescribeStackEventsRequest, DescribeStackEventsResult]) = ???

  override def describeStackInstanceAsync(describeStackInstanceRequest: DescribeStackInstanceRequest) = ???

  override def describeStackInstanceAsync(describeStackInstanceRequest: DescribeStackInstanceRequest,
                                          asyncHandler: AsyncHandler[DescribeStackInstanceRequest, DescribeStackInstanceResult]) = ???

  override def describeStackResourceAsync(describeStackResourceRequest: DescribeStackResourceRequest) = ???

  override def describeStackResourceAsync(describeStackResourceRequest: DescribeStackResourceRequest,
                                          asyncHandler: AsyncHandler[DescribeStackResourceRequest, DescribeStackResourceResult]) = ???

  override def describeStackResourcesAsync(describeStackResourcesRequest: DescribeStackResourcesRequest) = ???

  override def describeStackResourcesAsync(describeStackResourcesRequest: DescribeStackResourcesRequest,
                                           asyncHandler: AsyncHandler[DescribeStackResourcesRequest, DescribeStackResourcesResult]) = ???

  override def describeStackSetAsync(describeStackSetRequest: DescribeStackSetRequest) = ???

  override def describeStackSetAsync(describeStackSetRequest: DescribeStackSetRequest, asyncHandler: AsyncHandler[DescribeStackSetRequest, DescribeStackSetResult]) = ???

  override def describeStackSetOperationAsync(describeStackSetOperationRequest: DescribeStackSetOperationRequest) = ???

  override def describeStackSetOperationAsync(describeStackSetOperationRequest: DescribeStackSetOperationRequest,
                                              asyncHandler: AsyncHandler[DescribeStackSetOperationRequest, DescribeStackSetOperationResult]) = ???

  override def describeStacksAsync(describeStacksRequest: DescribeStacksRequest) = ???

  override def describeStacksAsync(describeStacksRequest: DescribeStacksRequest, asyncHandler: AsyncHandler[DescribeStacksRequest, DescribeStacksResult]) = ???

  override def describeStacksAsync() = ???

  override def describeStacksAsync(asyncHandler: AsyncHandler[DescribeStacksRequest, DescribeStacksResult]) = ???

  override def estimateTemplateCostAsync(estimateTemplateCostRequest: EstimateTemplateCostRequest) = ???

  override def estimateTemplateCostAsync(estimateTemplateCostRequest: EstimateTemplateCostRequest,
                                         asyncHandler: AsyncHandler[EstimateTemplateCostRequest, EstimateTemplateCostResult]) = ???

  override def estimateTemplateCostAsync() = ???

  override def estimateTemplateCostAsync(asyncHandler: AsyncHandler[EstimateTemplateCostRequest, EstimateTemplateCostResult]) = ???

  override def executeChangeSetAsync(executeChangeSetRequest: ExecuteChangeSetRequest) = ???

  override def executeChangeSetAsync(executeChangeSetRequest: ExecuteChangeSetRequest, asyncHandler: AsyncHandler[ExecuteChangeSetRequest, ExecuteChangeSetResult]) = ???

  override def getStackPolicyAsync(getStackPolicyRequest: GetStackPolicyRequest) = ???

  override def getStackPolicyAsync(getStackPolicyRequest: GetStackPolicyRequest, asyncHandler: AsyncHandler[GetStackPolicyRequest, GetStackPolicyResult]) = ???

  override def getTemplateAsync(getTemplateRequest: GetTemplateRequest) = ???

  override def getTemplateAsync(getTemplateRequest: GetTemplateRequest, asyncHandler: AsyncHandler[GetTemplateRequest, GetTemplateResult]) = ???

  override def getTemplateSummaryAsync(getTemplateSummaryRequest: GetTemplateSummaryRequest) = ???

  override def getTemplateSummaryAsync(getTemplateSummaryRequest: GetTemplateSummaryRequest, asyncHandler: AsyncHandler[GetTemplateSummaryRequest, GetTemplateSummaryResult]) = ???

  override def getTemplateSummaryAsync = ???

  override def getTemplateSummaryAsync(asyncHandler: AsyncHandler[GetTemplateSummaryRequest, GetTemplateSummaryResult]) = ???

  override def listChangeSetsAsync(listChangeSetsRequest: ListChangeSetsRequest) = ???

  override def listChangeSetsAsync(listChangeSetsRequest: ListChangeSetsRequest, asyncHandler: AsyncHandler[ListChangeSetsRequest, ListChangeSetsResult]) = ???

  override def listExportsAsync(listExportsRequest: ListExportsRequest) = ???

  override def listExportsAsync(listExportsRequest: ListExportsRequest, asyncHandler: AsyncHandler[ListExportsRequest, ListExportsResult]) = ???

  override def listImportsAsync(listImportsRequest: ListImportsRequest) = ???

  override def listImportsAsync(listImportsRequest: ListImportsRequest, asyncHandler: AsyncHandler[ListImportsRequest, ListImportsResult]) = ???

  override def listStackInstancesAsync(listStackInstancesRequest: ListStackInstancesRequest) = ???

  override def listStackInstancesAsync(listStackInstancesRequest: ListStackInstancesRequest, asyncHandler: AsyncHandler[ListStackInstancesRequest, ListStackInstancesResult]) = ???

  override def listStackResourcesAsync(listStackResourcesRequest: ListStackResourcesRequest) = ???

  override def listStackResourcesAsync(listStackResourcesRequest: ListStackResourcesRequest, asyncHandler: AsyncHandler[ListStackResourcesRequest, ListStackResourcesResult]) = ???

  override def listStackSetOperationResultsAsync(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest) = ???

  override def listStackSetOperationResultsAsync(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest,
                                                 asyncHandler: AsyncHandler[ListStackSetOperationResultsRequest, ListStackSetOperationResultsResult]) = ???

  override def listStackSetOperationsAsync(listStackSetOperationsRequest: ListStackSetOperationsRequest) = ???

  override def listStackSetOperationsAsync(listStackSetOperationsRequest: ListStackSetOperationsRequest,
                                           asyncHandler: AsyncHandler[ListStackSetOperationsRequest, ListStackSetOperationsResult]) = ???

  override def listStackSetsAsync(listStackSetsRequest: ListStackSetsRequest) = ???

  override def listStackSetsAsync(listStackSetsRequest: ListStackSetsRequest, asyncHandler: AsyncHandler[ListStackSetsRequest, ListStackSetsResult]) = ???

  override def listStacksAsync(listStacksRequest: ListStacksRequest) = ???

  override def listStacksAsync(listStacksRequest: ListStacksRequest, asyncHandler: AsyncHandler[ListStacksRequest, ListStacksResult]) = ???

  override def listStacksAsync() = ???

  override def listStacksAsync(asyncHandler: AsyncHandler[ListStacksRequest, ListStacksResult]) = ???

  override def setStackPolicyAsync(setStackPolicyRequest: SetStackPolicyRequest) = ???

  override def setStackPolicyAsync(setStackPolicyRequest: SetStackPolicyRequest, asyncHandler: AsyncHandler[SetStackPolicyRequest, SetStackPolicyResult]) = ???

  override def signalResourceAsync(signalResourceRequest: SignalResourceRequest) = ???

  override def signalResourceAsync(signalResourceRequest: SignalResourceRequest, asyncHandler: AsyncHandler[SignalResourceRequest, SignalResourceResult]) = ???

  override def stopStackSetOperationAsync(stopStackSetOperationRequest: StopStackSetOperationRequest) = ???

  override def stopStackSetOperationAsync(stopStackSetOperationRequest: StopStackSetOperationRequest,
                                          asyncHandler: AsyncHandler[StopStackSetOperationRequest, StopStackSetOperationResult]) = ???

  override def updateStackAsync(updateStackRequest: UpdateStackRequest) = ???

  override def updateStackAsync(updateStackRequest: UpdateStackRequest, asyncHandler: AsyncHandler[UpdateStackRequest, UpdateStackResult]) = ???

  override def updateStackInstancesAsync(updateStackInstancesRequest: UpdateStackInstancesRequest) = ???

  override def updateStackInstancesAsync(updateStackInstancesRequest: UpdateStackInstancesRequest,
                                         asyncHandler: AsyncHandler[UpdateStackInstancesRequest, UpdateStackInstancesResult]) = ???

  override def updateStackSetAsync(updateStackSetRequest: UpdateStackSetRequest) = ???

  override def updateStackSetAsync(updateStackSetRequest: UpdateStackSetRequest, asyncHandler: AsyncHandler[UpdateStackSetRequest, UpdateStackSetResult]) = ???

  override def updateTerminationProtectionAsync(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest) = ???

  override def updateTerminationProtectionAsync(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest,
                                                asyncHandler: AsyncHandler[UpdateTerminationProtectionRequest, UpdateTerminationProtectionResult]) = ???

  override def validateTemplateAsync(validateTemplateRequest: ValidateTemplateRequest) = ???

  override def validateTemplateAsync(validateTemplateRequest: ValidateTemplateRequest, asyncHandler: AsyncHandler[ValidateTemplateRequest, ValidateTemplateResult]) = ???

  override def setEndpoint(endpoint: String) = ???

  override def setRegion(region: Region) = ???

  override def cancelUpdateStack(cancelUpdateStackRequest: CancelUpdateStackRequest) = ???

  override def continueUpdateRollback(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest) = ???

  override def createChangeSet(createChangeSetRequest: CreateChangeSetRequest) = ???

  override def createStack(createStackRequest: CreateStackRequest) = ???

  override def createStackInstances(createStackInstancesRequest: CreateStackInstancesRequest) = ???

  override def createStackSet(createStackSetRequest: CreateStackSetRequest) = ???

  override def deleteChangeSet(deleteChangeSetRequest: DeleteChangeSetRequest) = ???

  override def deleteStack(deleteStackRequest: DeleteStackRequest) = ???

  override def deleteStackInstances(deleteStackInstancesRequest: DeleteStackInstancesRequest) = ???

  override def deleteStackSet(deleteStackSetRequest: DeleteStackSetRequest) = ???

  override def describeAccountLimits(describeAccountLimitsRequest: DescribeAccountLimitsRequest) = ???

  override def describeChangeSet(describeChangeSetRequest: DescribeChangeSetRequest) = ???

  override def describeStackEvents(describeStackEventsRequest: DescribeStackEventsRequest) = ???

  override def describeStackInstance(describeStackInstanceRequest: DescribeStackInstanceRequest) = ???

  override def describeStackResource(describeStackResourceRequest: DescribeStackResourceRequest) = ???

  override def describeStackResources(describeStackResourcesRequest: DescribeStackResourcesRequest) = ???

  override def describeStackSet(describeStackSetRequest: DescribeStackSetRequest) = ???

  override def describeStackSetOperation(describeStackSetOperationRequest: DescribeStackSetOperationRequest) = ???

  override def describeStacks(describeStacksRequest: DescribeStacksRequest) = ???

  override def describeStacks() = ???

  override def estimateTemplateCost(estimateTemplateCostRequest: EstimateTemplateCostRequest) = ???

  override def estimateTemplateCost() = ???

  override def executeChangeSet(executeChangeSetRequest: ExecuteChangeSetRequest) = ???

  override def getStackPolicy(getStackPolicyRequest: GetStackPolicyRequest) = ???

  override def getTemplate(getTemplateRequest: GetTemplateRequest) = ???

  override def getTemplateSummary(getTemplateSummaryRequest: GetTemplateSummaryRequest) = ???

  override def getTemplateSummary = ???

  override def listChangeSets(listChangeSetsRequest: ListChangeSetsRequest) = ???

  override def listExports(listExportsRequest: ListExportsRequest) = ???

  override def listImports(listImportsRequest: ListImportsRequest) = ???

  override def listStackInstances(listStackInstancesRequest: ListStackInstancesRequest) = ???

  override def listStackResources(listStackResourcesRequest: ListStackResourcesRequest) = ???

  override def listStackSetOperationResults(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest) = ???

  override def listStackSetOperations(listStackSetOperationsRequest: ListStackSetOperationsRequest) = ???

  override def listStackSets(listStackSetsRequest: ListStackSetsRequest) = ???

  override def listStacks(listStacksRequest: ListStacksRequest) = ???

  override def listStacks() = ???

  override def setStackPolicy(setStackPolicyRequest: SetStackPolicyRequest) = ???

  override def signalResource(signalResourceRequest: SignalResourceRequest) = ???

  override def stopStackSetOperation(stopStackSetOperationRequest: StopStackSetOperationRequest) = ???

  override def updateStack(updateStackRequest: UpdateStackRequest) = ???

  override def updateStackInstances(updateStackInstancesRequest: UpdateStackInstancesRequest) = ???

  override def updateStackSet(updateStackSetRequest: UpdateStackSetRequest) = ???

  override def updateTerminationProtection(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest) = ???

  override def validateTemplate(validateTemplateRequest: ValidateTemplateRequest) = ???

  override def shutdown() = ???

  override def getCachedResponseMetadata(request: AmazonWebServiceRequest) = ???

  override def waiters() = ???
}
