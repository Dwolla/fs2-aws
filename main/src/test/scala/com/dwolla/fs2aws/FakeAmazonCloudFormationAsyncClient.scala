package com.dwolla.fs2aws

import com.amazonaws.{AmazonWebServiceRequest, ResponseMetadata}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.Region
import com.amazonaws.services.cloudformation.AmazonCloudFormationAsync
import com.amazonaws.services.cloudformation.model._
import com.amazonaws.services.cloudformation.waiters.AmazonCloudFormationWaiters

import java.util.concurrent.Future

//noinspection NotImplementedCode,ScalaDeprecation
class FakeAmazonCloudFormationAsyncClient extends AmazonCloudFormationAsync {
  override def activateTypeAsync(activateTypeRequest: ActivateTypeRequest): Future[ActivateTypeResult] = ???

  override def activateTypeAsync(activateTypeRequest: ActivateTypeRequest, asyncHandler: AsyncHandler[ActivateTypeRequest, ActivateTypeResult]): Future[ActivateTypeResult] = ???

  override def batchDescribeTypeConfigurationsAsync(batchDescribeTypeConfigurationsRequest: BatchDescribeTypeConfigurationsRequest): Future[BatchDescribeTypeConfigurationsResult] = ???

  override def batchDescribeTypeConfigurationsAsync(batchDescribeTypeConfigurationsRequest: BatchDescribeTypeConfigurationsRequest, asyncHandler: AsyncHandler[BatchDescribeTypeConfigurationsRequest, BatchDescribeTypeConfigurationsResult]): Future[BatchDescribeTypeConfigurationsResult] = ???

  override def deactivateTypeAsync(deactivateTypeRequest: DeactivateTypeRequest): Future[DeactivateTypeResult] = ???

  override def deactivateTypeAsync(deactivateTypeRequest: DeactivateTypeRequest, asyncHandler: AsyncHandler[DeactivateTypeRequest, DeactivateTypeResult]): Future[DeactivateTypeResult] = ???

  override def describePublisherAsync(describePublisherRequest: DescribePublisherRequest): Future[DescribePublisherResult] = ???

  override def describePublisherAsync(describePublisherRequest: DescribePublisherRequest, asyncHandler: AsyncHandler[DescribePublisherRequest, DescribePublisherResult]): Future[DescribePublisherResult] = ???

  override def importStacksToStackSetAsync(importStacksToStackSetRequest: ImportStacksToStackSetRequest): Future[ImportStacksToStackSetResult] = ???

  override def importStacksToStackSetAsync(importStacksToStackSetRequest: ImportStacksToStackSetRequest, asyncHandler: AsyncHandler[ImportStacksToStackSetRequest, ImportStacksToStackSetResult]): Future[ImportStacksToStackSetResult] = ???

  override def publishTypeAsync(publishTypeRequest: PublishTypeRequest): Future[PublishTypeResult] = ???

  override def publishTypeAsync(publishTypeRequest: PublishTypeRequest, asyncHandler: AsyncHandler[PublishTypeRequest, PublishTypeResult]): Future[PublishTypeResult] = ???

  override def registerPublisherAsync(registerPublisherRequest: RegisterPublisherRequest): Future[RegisterPublisherResult] = ???

  override def registerPublisherAsync(registerPublisherRequest: RegisterPublisherRequest, asyncHandler: AsyncHandler[RegisterPublisherRequest, RegisterPublisherResult]): Future[RegisterPublisherResult] = ???

  override def rollbackStackAsync(rollbackStackRequest: RollbackStackRequest): Future[RollbackStackResult] = ???

  override def rollbackStackAsync(rollbackStackRequest: RollbackStackRequest, asyncHandler: AsyncHandler[RollbackStackRequest, RollbackStackResult]): Future[RollbackStackResult] = ???

  override def setTypeConfigurationAsync(setTypeConfigurationRequest: SetTypeConfigurationRequest): Future[SetTypeConfigurationResult] = ???

  override def setTypeConfigurationAsync(setTypeConfigurationRequest: SetTypeConfigurationRequest, asyncHandler: AsyncHandler[SetTypeConfigurationRequest, SetTypeConfigurationResult]): Future[SetTypeConfigurationResult] = ???

  override def testTypeAsync(testTypeRequest: TestTypeRequest): Future[TestTypeResult] = ???

  override def testTypeAsync(testTypeRequest: TestTypeRequest, asyncHandler: AsyncHandler[TestTypeRequest, TestTypeResult]): Future[TestTypeResult] = ???

  override def activateType(activateTypeRequest: ActivateTypeRequest): ActivateTypeResult = ???

  override def batchDescribeTypeConfigurations(batchDescribeTypeConfigurationsRequest: BatchDescribeTypeConfigurationsRequest): BatchDescribeTypeConfigurationsResult = ???

  override def deactivateType(deactivateTypeRequest: DeactivateTypeRequest): DeactivateTypeResult = ???

  override def describePublisher(describePublisherRequest: DescribePublisherRequest): DescribePublisherResult = ???

  override def importStacksToStackSet(importStacksToStackSetRequest: ImportStacksToStackSetRequest): ImportStacksToStackSetResult = ???

  override def publishType(publishTypeRequest: PublishTypeRequest): PublishTypeResult = ???

  override def registerPublisher(registerPublisherRequest: RegisterPublisherRequest): RegisterPublisherResult = ???

  override def rollbackStack(rollbackStackRequest: RollbackStackRequest): RollbackStackResult = ???

  override def setTypeConfiguration(setTypeConfigurationRequest: SetTypeConfigurationRequest): SetTypeConfigurationResult = ???

  override def testType(testTypeRequest: TestTypeRequest): TestTypeResult = ???

  override def cancelUpdateStackAsync(cancelUpdateStackRequest: CancelUpdateStackRequest): Future[CancelUpdateStackResult] = ???

  override def cancelUpdateStackAsync(cancelUpdateStackRequest: CancelUpdateStackRequest, asyncHandler: AsyncHandler[CancelUpdateStackRequest, CancelUpdateStackResult]): Future[CancelUpdateStackResult] = ???

  override def continueUpdateRollbackAsync(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest): Future[ContinueUpdateRollbackResult] = ???

  override def continueUpdateRollbackAsync(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest, asyncHandler: AsyncHandler[ContinueUpdateRollbackRequest, ContinueUpdateRollbackResult]): Future[ContinueUpdateRollbackResult] = ???

  override def createChangeSetAsync(createChangeSetRequest: CreateChangeSetRequest): Future[CreateChangeSetResult] = ???

  override def createChangeSetAsync(createChangeSetRequest: CreateChangeSetRequest, asyncHandler: AsyncHandler[CreateChangeSetRequest, CreateChangeSetResult]): Future[CreateChangeSetResult] = ???

  override def createStackAsync(createStackRequest: CreateStackRequest): Future[CreateStackResult] = ???

  override def createStackAsync(createStackRequest: CreateStackRequest, asyncHandler: AsyncHandler[CreateStackRequest, CreateStackResult]): Future[CreateStackResult] = ???

  override def createStackInstancesAsync(createStackInstancesRequest: CreateStackInstancesRequest): Future[CreateStackInstancesResult] = ???

  override def createStackInstancesAsync(createStackInstancesRequest: CreateStackInstancesRequest, asyncHandler: AsyncHandler[CreateStackInstancesRequest, CreateStackInstancesResult]): Future[CreateStackInstancesResult] = ???

  override def createStackSetAsync(createStackSetRequest: CreateStackSetRequest): Future[CreateStackSetResult] = ???

  override def createStackSetAsync(createStackSetRequest: CreateStackSetRequest, asyncHandler: AsyncHandler[CreateStackSetRequest, CreateStackSetResult]): Future[CreateStackSetResult] = ???

  override def deleteChangeSetAsync(deleteChangeSetRequest: DeleteChangeSetRequest): Future[DeleteChangeSetResult] = ???

  override def deleteChangeSetAsync(deleteChangeSetRequest: DeleteChangeSetRequest, asyncHandler: AsyncHandler[DeleteChangeSetRequest, DeleteChangeSetResult]): Future[DeleteChangeSetResult] = ???

  override def deleteStackAsync(deleteStackRequest: DeleteStackRequest): Future[DeleteStackResult] = ???

  override def deleteStackAsync(deleteStackRequest: DeleteStackRequest, asyncHandler: AsyncHandler[DeleteStackRequest, DeleteStackResult]): Future[DeleteStackResult] = ???

  override def deleteStackInstancesAsync(deleteStackInstancesRequest: DeleteStackInstancesRequest): Future[DeleteStackInstancesResult] = ???

  override def deleteStackInstancesAsync(deleteStackInstancesRequest: DeleteStackInstancesRequest, asyncHandler: AsyncHandler[DeleteStackInstancesRequest, DeleteStackInstancesResult]): Future[DeleteStackInstancesResult] = ???

  override def deleteStackSetAsync(deleteStackSetRequest: DeleteStackSetRequest): Future[DeleteStackSetResult] = ???

  override def deleteStackSetAsync(deleteStackSetRequest: DeleteStackSetRequest, asyncHandler: AsyncHandler[DeleteStackSetRequest, DeleteStackSetResult]): Future[DeleteStackSetResult] = ???

  override def deregisterTypeAsync(deregisterTypeRequest: DeregisterTypeRequest): Future[DeregisterTypeResult] = ???

  override def deregisterTypeAsync(deregisterTypeRequest: DeregisterTypeRequest, asyncHandler: AsyncHandler[DeregisterTypeRequest, DeregisterTypeResult]): Future[DeregisterTypeResult] = ???

  override def describeAccountLimitsAsync(describeAccountLimitsRequest: DescribeAccountLimitsRequest): Future[DescribeAccountLimitsResult] = ???

  override def describeAccountLimitsAsync(describeAccountLimitsRequest: DescribeAccountLimitsRequest, asyncHandler: AsyncHandler[DescribeAccountLimitsRequest, DescribeAccountLimitsResult]): Future[DescribeAccountLimitsResult] = ???

  override def describeChangeSetAsync(describeChangeSetRequest: DescribeChangeSetRequest): Future[DescribeChangeSetResult] = ???

  override def describeChangeSetAsync(describeChangeSetRequest: DescribeChangeSetRequest, asyncHandler: AsyncHandler[DescribeChangeSetRequest, DescribeChangeSetResult]): Future[DescribeChangeSetResult] = ???

  override def describeStackDriftDetectionStatusAsync(describeStackDriftDetectionStatusRequest: DescribeStackDriftDetectionStatusRequest): Future[DescribeStackDriftDetectionStatusResult] = ???

  override def describeStackDriftDetectionStatusAsync(describeStackDriftDetectionStatusRequest: DescribeStackDriftDetectionStatusRequest, asyncHandler: AsyncHandler[DescribeStackDriftDetectionStatusRequest, DescribeStackDriftDetectionStatusResult]): Future[DescribeStackDriftDetectionStatusResult] = ???

  override def describeStackEventsAsync(describeStackEventsRequest: DescribeStackEventsRequest): Future[DescribeStackEventsResult] = ???

  override def describeStackEventsAsync(describeStackEventsRequest: DescribeStackEventsRequest, asyncHandler: AsyncHandler[DescribeStackEventsRequest, DescribeStackEventsResult]): Future[DescribeStackEventsResult] = ???

  override def describeStackInstanceAsync(describeStackInstanceRequest: DescribeStackInstanceRequest): Future[DescribeStackInstanceResult] = ???

  override def describeStackInstanceAsync(describeStackInstanceRequest: DescribeStackInstanceRequest, asyncHandler: AsyncHandler[DescribeStackInstanceRequest, DescribeStackInstanceResult]): Future[DescribeStackInstanceResult] = ???

  override def describeStackResourceAsync(describeStackResourceRequest: DescribeStackResourceRequest): Future[DescribeStackResourceResult] = ???

  override def describeStackResourceAsync(describeStackResourceRequest: DescribeStackResourceRequest, asyncHandler: AsyncHandler[DescribeStackResourceRequest, DescribeStackResourceResult]): Future[DescribeStackResourceResult] = ???

  override def describeStackResourceDriftsAsync(describeStackResourceDriftsRequest: DescribeStackResourceDriftsRequest): Future[DescribeStackResourceDriftsResult] = ???

  override def describeStackResourceDriftsAsync(describeStackResourceDriftsRequest: DescribeStackResourceDriftsRequest, asyncHandler: AsyncHandler[DescribeStackResourceDriftsRequest, DescribeStackResourceDriftsResult]): Future[DescribeStackResourceDriftsResult] = ???

  override def describeStackResourcesAsync(describeStackResourcesRequest: DescribeStackResourcesRequest): Future[DescribeStackResourcesResult] = ???

  override def describeStackResourcesAsync(describeStackResourcesRequest: DescribeStackResourcesRequest, asyncHandler: AsyncHandler[DescribeStackResourcesRequest, DescribeStackResourcesResult]): Future[DescribeStackResourcesResult] = ???

  override def describeStackSetAsync(describeStackSetRequest: DescribeStackSetRequest): Future[DescribeStackSetResult] = ???

  override def describeStackSetAsync(describeStackSetRequest: DescribeStackSetRequest, asyncHandler: AsyncHandler[DescribeStackSetRequest, DescribeStackSetResult]): Future[DescribeStackSetResult] = ???

  override def describeStackSetOperationAsync(describeStackSetOperationRequest: DescribeStackSetOperationRequest): Future[DescribeStackSetOperationResult] = ???

  override def describeStackSetOperationAsync(describeStackSetOperationRequest: DescribeStackSetOperationRequest, asyncHandler: AsyncHandler[DescribeStackSetOperationRequest, DescribeStackSetOperationResult]): Future[DescribeStackSetOperationResult] = ???

  override def describeStacksAsync(describeStacksRequest: DescribeStacksRequest): Future[DescribeStacksResult] = ???

  override def describeStacksAsync(describeStacksRequest: DescribeStacksRequest, asyncHandler: AsyncHandler[DescribeStacksRequest, DescribeStacksResult]): Future[DescribeStacksResult] = ???

  override def describeStacksAsync(): Future[DescribeStacksResult] = ???

  override def describeStacksAsync(asyncHandler: AsyncHandler[DescribeStacksRequest, DescribeStacksResult]): Future[DescribeStacksResult] = ???

  override def describeTypeAsync(describeTypeRequest: DescribeTypeRequest): Future[DescribeTypeResult] = ???

  override def describeTypeAsync(describeTypeRequest: DescribeTypeRequest, asyncHandler: AsyncHandler[DescribeTypeRequest, DescribeTypeResult]): Future[DescribeTypeResult] = ???

  override def describeTypeRegistrationAsync(describeTypeRegistrationRequest: DescribeTypeRegistrationRequest): Future[DescribeTypeRegistrationResult] = ???

  override def describeTypeRegistrationAsync(describeTypeRegistrationRequest: DescribeTypeRegistrationRequest, asyncHandler: AsyncHandler[DescribeTypeRegistrationRequest, DescribeTypeRegistrationResult]): Future[DescribeTypeRegistrationResult] = ???

  override def detectStackDriftAsync(detectStackDriftRequest: DetectStackDriftRequest): Future[DetectStackDriftResult] = ???

  override def detectStackDriftAsync(detectStackDriftRequest: DetectStackDriftRequest, asyncHandler: AsyncHandler[DetectStackDriftRequest, DetectStackDriftResult]): Future[DetectStackDriftResult] = ???

  override def detectStackResourceDriftAsync(detectStackResourceDriftRequest: DetectStackResourceDriftRequest): Future[DetectStackResourceDriftResult] = ???

  override def detectStackResourceDriftAsync(detectStackResourceDriftRequest: DetectStackResourceDriftRequest, asyncHandler: AsyncHandler[DetectStackResourceDriftRequest, DetectStackResourceDriftResult]): Future[DetectStackResourceDriftResult] = ???

  override def detectStackSetDriftAsync(detectStackSetDriftRequest: DetectStackSetDriftRequest): Future[DetectStackSetDriftResult] = ???

  override def detectStackSetDriftAsync(detectStackSetDriftRequest: DetectStackSetDriftRequest, asyncHandler: AsyncHandler[DetectStackSetDriftRequest, DetectStackSetDriftResult]): Future[DetectStackSetDriftResult] = ???

  override def estimateTemplateCostAsync(estimateTemplateCostRequest: EstimateTemplateCostRequest): Future[EstimateTemplateCostResult] = ???

  override def estimateTemplateCostAsync(estimateTemplateCostRequest: EstimateTemplateCostRequest, asyncHandler: AsyncHandler[EstimateTemplateCostRequest, EstimateTemplateCostResult]): Future[EstimateTemplateCostResult] = ???

  override def estimateTemplateCostAsync(): Future[EstimateTemplateCostResult] = ???

  override def estimateTemplateCostAsync(asyncHandler: AsyncHandler[EstimateTemplateCostRequest, EstimateTemplateCostResult]): Future[EstimateTemplateCostResult] = ???

  override def executeChangeSetAsync(executeChangeSetRequest: ExecuteChangeSetRequest): Future[ExecuteChangeSetResult] = ???

  override def executeChangeSetAsync(executeChangeSetRequest: ExecuteChangeSetRequest, asyncHandler: AsyncHandler[ExecuteChangeSetRequest, ExecuteChangeSetResult]): Future[ExecuteChangeSetResult] = ???

  override def getStackPolicyAsync(getStackPolicyRequest: GetStackPolicyRequest): Future[GetStackPolicyResult] = ???

  override def getStackPolicyAsync(getStackPolicyRequest: GetStackPolicyRequest, asyncHandler: AsyncHandler[GetStackPolicyRequest, GetStackPolicyResult]): Future[GetStackPolicyResult] = ???

  override def getTemplateAsync(getTemplateRequest: GetTemplateRequest): Future[GetTemplateResult] = ???

  override def getTemplateAsync(getTemplateRequest: GetTemplateRequest, asyncHandler: AsyncHandler[GetTemplateRequest, GetTemplateResult]): Future[GetTemplateResult] = ???

  override def getTemplateSummaryAsync(getTemplateSummaryRequest: GetTemplateSummaryRequest): Future[GetTemplateSummaryResult] = ???

  override def getTemplateSummaryAsync(getTemplateSummaryRequest: GetTemplateSummaryRequest, asyncHandler: AsyncHandler[GetTemplateSummaryRequest, GetTemplateSummaryResult]): Future[GetTemplateSummaryResult] = ???

  override def getTemplateSummaryAsync: Future[GetTemplateSummaryResult] = ???

  override def getTemplateSummaryAsync(asyncHandler: AsyncHandler[GetTemplateSummaryRequest, GetTemplateSummaryResult]): Future[GetTemplateSummaryResult] = ???

  override def listChangeSetsAsync(listChangeSetsRequest: ListChangeSetsRequest): Future[ListChangeSetsResult] = ???

  override def listChangeSetsAsync(listChangeSetsRequest: ListChangeSetsRequest, asyncHandler: AsyncHandler[ListChangeSetsRequest, ListChangeSetsResult]): Future[ListChangeSetsResult] = ???

  override def listExportsAsync(listExportsRequest: ListExportsRequest): Future[ListExportsResult] = ???

  override def listExportsAsync(listExportsRequest: ListExportsRequest, asyncHandler: AsyncHandler[ListExportsRequest, ListExportsResult]): Future[ListExportsResult] = ???

  override def listImportsAsync(listImportsRequest: ListImportsRequest): Future[ListImportsResult] = ???

  override def listImportsAsync(listImportsRequest: ListImportsRequest, asyncHandler: AsyncHandler[ListImportsRequest, ListImportsResult]): Future[ListImportsResult] = ???

  override def listStackInstancesAsync(listStackInstancesRequest: ListStackInstancesRequest): Future[ListStackInstancesResult] = ???

  override def listStackInstancesAsync(listStackInstancesRequest: ListStackInstancesRequest, asyncHandler: AsyncHandler[ListStackInstancesRequest, ListStackInstancesResult]): Future[ListStackInstancesResult] = ???

  override def listStackResourcesAsync(listStackResourcesRequest: ListStackResourcesRequest): Future[ListStackResourcesResult] = ???

  override def listStackResourcesAsync(listStackResourcesRequest: ListStackResourcesRequest, asyncHandler: AsyncHandler[ListStackResourcesRequest, ListStackResourcesResult]): Future[ListStackResourcesResult] = ???

  override def listStackSetOperationResultsAsync(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest): Future[ListStackSetOperationResultsResult] = ???

  override def listStackSetOperationResultsAsync(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest, asyncHandler: AsyncHandler[ListStackSetOperationResultsRequest, ListStackSetOperationResultsResult]): Future[ListStackSetOperationResultsResult] = ???

  override def listStackSetOperationsAsync(listStackSetOperationsRequest: ListStackSetOperationsRequest): Future[ListStackSetOperationsResult] = ???

  override def listStackSetOperationsAsync(listStackSetOperationsRequest: ListStackSetOperationsRequest, asyncHandler: AsyncHandler[ListStackSetOperationsRequest, ListStackSetOperationsResult]): Future[ListStackSetOperationsResult] = ???

  override def listStackSetsAsync(listStackSetsRequest: ListStackSetsRequest): Future[ListStackSetsResult] = ???

  override def listStackSetsAsync(listStackSetsRequest: ListStackSetsRequest, asyncHandler: AsyncHandler[ListStackSetsRequest, ListStackSetsResult]): Future[ListStackSetsResult] = ???

  override def listStacksAsync(listStacksRequest: ListStacksRequest): Future[ListStacksResult] = ???

  override def listStacksAsync(listStacksRequest: ListStacksRequest, asyncHandler: AsyncHandler[ListStacksRequest, ListStacksResult]): Future[ListStacksResult] = ???

  override def listStacksAsync(): Future[ListStacksResult] = ???

  override def listStacksAsync(asyncHandler: AsyncHandler[ListStacksRequest, ListStacksResult]): Future[ListStacksResult] = ???

  override def listTypeRegistrationsAsync(listTypeRegistrationsRequest: ListTypeRegistrationsRequest): Future[ListTypeRegistrationsResult] = ???

  override def listTypeRegistrationsAsync(listTypeRegistrationsRequest: ListTypeRegistrationsRequest, asyncHandler: AsyncHandler[ListTypeRegistrationsRequest, ListTypeRegistrationsResult]): Future[ListTypeRegistrationsResult] = ???

  override def listTypeVersionsAsync(listTypeVersionsRequest: ListTypeVersionsRequest): Future[ListTypeVersionsResult] = ???

  override def listTypeVersionsAsync(listTypeVersionsRequest: ListTypeVersionsRequest, asyncHandler: AsyncHandler[ListTypeVersionsRequest, ListTypeVersionsResult]): Future[ListTypeVersionsResult] = ???

  override def listTypesAsync(listTypesRequest: ListTypesRequest): Future[ListTypesResult] = ???

  override def listTypesAsync(listTypesRequest: ListTypesRequest, asyncHandler: AsyncHandler[ListTypesRequest, ListTypesResult]): Future[ListTypesResult] = ???

  override def recordHandlerProgressAsync(recordHandlerProgressRequest: RecordHandlerProgressRequest): Future[RecordHandlerProgressResult] = ???

  override def recordHandlerProgressAsync(recordHandlerProgressRequest: RecordHandlerProgressRequest, asyncHandler: AsyncHandler[RecordHandlerProgressRequest, RecordHandlerProgressResult]): Future[RecordHandlerProgressResult] = ???

  override def registerTypeAsync(registerTypeRequest: RegisterTypeRequest): Future[RegisterTypeResult] = ???

  override def registerTypeAsync(registerTypeRequest: RegisterTypeRequest, asyncHandler: AsyncHandler[RegisterTypeRequest, RegisterTypeResult]): Future[RegisterTypeResult] = ???

  override def setStackPolicyAsync(setStackPolicyRequest: SetStackPolicyRequest): Future[SetStackPolicyResult] = ???

  override def setStackPolicyAsync(setStackPolicyRequest: SetStackPolicyRequest, asyncHandler: AsyncHandler[SetStackPolicyRequest, SetStackPolicyResult]): Future[SetStackPolicyResult] = ???

  override def setTypeDefaultVersionAsync(setTypeDefaultVersionRequest: SetTypeDefaultVersionRequest): Future[SetTypeDefaultVersionResult] = ???

  override def setTypeDefaultVersionAsync(setTypeDefaultVersionRequest: SetTypeDefaultVersionRequest, asyncHandler: AsyncHandler[SetTypeDefaultVersionRequest, SetTypeDefaultVersionResult]): Future[SetTypeDefaultVersionResult] = ???

  override def signalResourceAsync(signalResourceRequest: SignalResourceRequest): Future[SignalResourceResult] = ???

  override def signalResourceAsync(signalResourceRequest: SignalResourceRequest, asyncHandler: AsyncHandler[SignalResourceRequest, SignalResourceResult]): Future[SignalResourceResult] = ???

  override def stopStackSetOperationAsync(stopStackSetOperationRequest: StopStackSetOperationRequest): Future[StopStackSetOperationResult] = ???

  override def stopStackSetOperationAsync(stopStackSetOperationRequest: StopStackSetOperationRequest, asyncHandler: AsyncHandler[StopStackSetOperationRequest, StopStackSetOperationResult]): Future[StopStackSetOperationResult] = ???

  override def updateStackAsync(updateStackRequest: UpdateStackRequest): Future[UpdateStackResult] = ???

  override def updateStackAsync(updateStackRequest: UpdateStackRequest, asyncHandler: AsyncHandler[UpdateStackRequest, UpdateStackResult]): Future[UpdateStackResult] = ???

  override def updateStackInstancesAsync(updateStackInstancesRequest: UpdateStackInstancesRequest): Future[UpdateStackInstancesResult] = ???

  override def updateStackInstancesAsync(updateStackInstancesRequest: UpdateStackInstancesRequest, asyncHandler: AsyncHandler[UpdateStackInstancesRequest, UpdateStackInstancesResult]): Future[UpdateStackInstancesResult] = ???

  override def updateStackSetAsync(updateStackSetRequest: UpdateStackSetRequest): Future[UpdateStackSetResult] = ???

  override def updateStackSetAsync(updateStackSetRequest: UpdateStackSetRequest, asyncHandler: AsyncHandler[UpdateStackSetRequest, UpdateStackSetResult]): Future[UpdateStackSetResult] = ???

  override def updateTerminationProtectionAsync(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest): Future[UpdateTerminationProtectionResult] = ???

  override def updateTerminationProtectionAsync(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest, asyncHandler: AsyncHandler[UpdateTerminationProtectionRequest, UpdateTerminationProtectionResult]): Future[UpdateTerminationProtectionResult] = ???

  override def validateTemplateAsync(validateTemplateRequest: ValidateTemplateRequest): Future[ValidateTemplateResult] = ???

  override def validateTemplateAsync(validateTemplateRequest: ValidateTemplateRequest, asyncHandler: AsyncHandler[ValidateTemplateRequest, ValidateTemplateResult]): Future[ValidateTemplateResult] = ???

  override def setEndpoint(endpoint: String): Unit = ???

  override def setRegion(region: Region): Unit = ???

  override def cancelUpdateStack(cancelUpdateStackRequest: CancelUpdateStackRequest): CancelUpdateStackResult = ???

  override def continueUpdateRollback(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest): ContinueUpdateRollbackResult = ???

  override def createChangeSet(createChangeSetRequest: CreateChangeSetRequest): CreateChangeSetResult = ???

  override def createStack(createStackRequest: CreateStackRequest): CreateStackResult = ???

  override def createStackInstances(createStackInstancesRequest: CreateStackInstancesRequest): CreateStackInstancesResult = ???

  override def createStackSet(createStackSetRequest: CreateStackSetRequest): CreateStackSetResult = ???

  override def deleteChangeSet(deleteChangeSetRequest: DeleteChangeSetRequest): DeleteChangeSetResult = ???

  override def deleteStack(deleteStackRequest: DeleteStackRequest): DeleteStackResult = ???

  override def deleteStackInstances(deleteStackInstancesRequest: DeleteStackInstancesRequest): DeleteStackInstancesResult = ???

  override def deleteStackSet(deleteStackSetRequest: DeleteStackSetRequest): DeleteStackSetResult = ???

  override def deregisterType(deregisterTypeRequest: DeregisterTypeRequest): DeregisterTypeResult = ???

  override def describeAccountLimits(describeAccountLimitsRequest: DescribeAccountLimitsRequest): DescribeAccountLimitsResult = ???

  override def describeChangeSet(describeChangeSetRequest: DescribeChangeSetRequest): DescribeChangeSetResult = ???

  override def describeStackDriftDetectionStatus(describeStackDriftDetectionStatusRequest: DescribeStackDriftDetectionStatusRequest): DescribeStackDriftDetectionStatusResult = ???

  override def describeStackEvents(describeStackEventsRequest: DescribeStackEventsRequest): DescribeStackEventsResult = ???

  override def describeStackInstance(describeStackInstanceRequest: DescribeStackInstanceRequest): DescribeStackInstanceResult = ???

  override def describeStackResource(describeStackResourceRequest: DescribeStackResourceRequest): DescribeStackResourceResult = ???

  override def describeStackResourceDrifts(describeStackResourceDriftsRequest: DescribeStackResourceDriftsRequest): DescribeStackResourceDriftsResult = ???

  override def describeStackResources(describeStackResourcesRequest: DescribeStackResourcesRequest): DescribeStackResourcesResult = ???

  override def describeStackSet(describeStackSetRequest: DescribeStackSetRequest): DescribeStackSetResult = ???

  override def describeStackSetOperation(describeStackSetOperationRequest: DescribeStackSetOperationRequest): DescribeStackSetOperationResult = ???

  override def describeStacks(describeStacksRequest: DescribeStacksRequest): DescribeStacksResult = ???

  override def describeStacks(): DescribeStacksResult = ???

  override def describeType(describeTypeRequest: DescribeTypeRequest): DescribeTypeResult = ???

  override def describeTypeRegistration(describeTypeRegistrationRequest: DescribeTypeRegistrationRequest): DescribeTypeRegistrationResult = ???

  override def detectStackDrift(detectStackDriftRequest: DetectStackDriftRequest): DetectStackDriftResult = ???

  override def detectStackResourceDrift(detectStackResourceDriftRequest: DetectStackResourceDriftRequest): DetectStackResourceDriftResult = ???

  override def detectStackSetDrift(detectStackSetDriftRequest: DetectStackSetDriftRequest): DetectStackSetDriftResult = ???

  override def estimateTemplateCost(estimateTemplateCostRequest: EstimateTemplateCostRequest): EstimateTemplateCostResult = ???

  override def estimateTemplateCost(): EstimateTemplateCostResult = ???

  override def executeChangeSet(executeChangeSetRequest: ExecuteChangeSetRequest): ExecuteChangeSetResult = ???

  override def getStackPolicy(getStackPolicyRequest: GetStackPolicyRequest): GetStackPolicyResult = ???

  override def getTemplate(getTemplateRequest: GetTemplateRequest): GetTemplateResult = ???

  override def getTemplateSummary(getTemplateSummaryRequest: GetTemplateSummaryRequest): GetTemplateSummaryResult = ???

  override def getTemplateSummary: GetTemplateSummaryResult = ???

  override def listChangeSets(listChangeSetsRequest: ListChangeSetsRequest): ListChangeSetsResult = ???

  override def listExports(listExportsRequest: ListExportsRequest): ListExportsResult = ???

  override def listImports(listImportsRequest: ListImportsRequest): ListImportsResult = ???

  override def listStackInstances(listStackInstancesRequest: ListStackInstancesRequest): ListStackInstancesResult = ???

  override def listStackResources(listStackResourcesRequest: ListStackResourcesRequest): ListStackResourcesResult = ???

  override def listStackSetOperationResults(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest): ListStackSetOperationResultsResult = ???

  override def listStackSetOperations(listStackSetOperationsRequest: ListStackSetOperationsRequest): ListStackSetOperationsResult = ???

  override def listStackSets(listStackSetsRequest: ListStackSetsRequest): ListStackSetsResult = ???

  override def listStacks(listStacksRequest: ListStacksRequest): ListStacksResult = ???

  override def listStacks(): ListStacksResult = ???

  override def listTypeRegistrations(listTypeRegistrationsRequest: ListTypeRegistrationsRequest): ListTypeRegistrationsResult = ???

  override def listTypeVersions(listTypeVersionsRequest: ListTypeVersionsRequest): ListTypeVersionsResult = ???

  override def listTypes(listTypesRequest: ListTypesRequest): ListTypesResult = ???

  override def recordHandlerProgress(recordHandlerProgressRequest: RecordHandlerProgressRequest): RecordHandlerProgressResult = ???

  override def registerType(registerTypeRequest: RegisterTypeRequest): RegisterTypeResult = ???

  override def setStackPolicy(setStackPolicyRequest: SetStackPolicyRequest): SetStackPolicyResult = ???

  override def setTypeDefaultVersion(setTypeDefaultVersionRequest: SetTypeDefaultVersionRequest): SetTypeDefaultVersionResult = ???

  override def signalResource(signalResourceRequest: SignalResourceRequest): SignalResourceResult = ???

  override def stopStackSetOperation(stopStackSetOperationRequest: StopStackSetOperationRequest): StopStackSetOperationResult = ???

  override def updateStack(updateStackRequest: UpdateStackRequest): UpdateStackResult = ???

  override def updateStackInstances(updateStackInstancesRequest: UpdateStackInstancesRequest): UpdateStackInstancesResult = ???

  override def updateStackSet(updateStackSetRequest: UpdateStackSetRequest): UpdateStackSetResult = ???

  override def updateTerminationProtection(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest): UpdateTerminationProtectionResult = ???

  override def validateTemplate(validateTemplateRequest: ValidateTemplateRequest): ValidateTemplateResult = ???

  override def shutdown(): Unit = ???

  override def getCachedResponseMetadata(request: AmazonWebServiceRequest): ResponseMetadata = ???

  override def waiters(): AmazonCloudFormationWaiters = ???
}
