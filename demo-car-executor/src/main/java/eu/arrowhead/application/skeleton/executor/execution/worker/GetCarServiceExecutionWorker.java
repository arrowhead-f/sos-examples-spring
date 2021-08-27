package eu.arrowhead.application.skeleton.executor.execution.worker;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import ai.aitia.demo.car_common.dto.CarRequestDTO;
import ai.aitia.demo.car_common.dto.CarResponseDTO;
import ai.aitia.demo.car_executor.model.service.GetCarServiceModel;
import eu.arrowhead.application.skeleton.executor.execution.ExecutionBoard;
import eu.arrowhead.application.skeleton.executor.execution.ExecutionSignal;
import eu.arrowhead.application.skeleton.executor.execution.Job;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.ChoreographerExecuteStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

public class GetCarServiceExecutionWorker implements Runnable {

	//=================================================================================================
	// members
	
	private final Job job;
	
	@Autowired
	private ExecutionBoard board;
	
	@Autowired
	private ExecutorDriver driver;
	
	@Autowired
	private GetCarServiceModel serviceModel;
	
	private static final String PARAMETER_BRAND = "brand";
	private static final String PARAMETER_COLOR = "color";
	
	private final Logger logger = LogManager.getLogger(GetCarServiceExecutionWorker.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public GetCarServiceExecutionWorker(final Job job) {
		Assert.notNull(job, "job is null");
		this.job = job;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
			board.removeJob(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId());
			return;
		}
		
		try {
			validateJob();			
			consumeCreateCarService();
			
			if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
				board.removeJob(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId());
				return;
			}
			
			consumeGetCarService();	
			
		} catch (final Exception ex) {
			logger.debug(ex);
			notifyChoreographer(ChoreographerExecutedStepStatus.ERROR, ex.getMessage(), ex.getClass().getSimpleName());
		}
		
		notifyChoreographer(ChoreographerExecutedStepStatus.SUCCESS);
	}
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private void validateJob() {
		final ChoreographerExecuteStepRequestDTO jobRequest = job.getJobRequest();
		final ChoreographerExecutorServiceInfoResponseDTO serviceInfo = serviceModel.createChoreographerExecutorServiceInfoResponseDTO();
		
		Assert.isTrue(jobRequest.getMainOrchestrationResult().getService().getServiceDefinition().equalsIgnoreCase(serviceInfo.getServiceDefinition()),
					  "main service missmatch");
		
		final Integer mainServiceProvidedVersion = jobRequest.getMainOrchestrationResult().getVersion();
		final int mainServiceRequiredMinVersion = serviceInfo.getMinVersion();
		final int mainServiceRequiredMaxVersion = serviceInfo.getMaxVersion();
		if (mainServiceProvidedVersion != null
				&& mainServiceProvidedVersion < mainServiceRequiredMinVersion
				&& mainServiceProvidedVersion > mainServiceRequiredMaxVersion) {
			throw new InvalidParameterException("main service version not supported");
		}
		
		if (jobRequest.getPreconditionOrchestrationResults() == null
				|| jobRequest.getPreconditionOrchestrationResults().isEmpty()
				|| jobRequest.getPreconditionOrchestrationResults().size() > 1) {
			throw new InvalidParameterException("preconditionOrchestrationResults list is empty or more than 1");
		}
		
		final String providedPreconditionService = jobRequest.getPreconditionOrchestrationResults().get(0).getService().getServiceDefinition();
		final String requiredPreconditionService = serviceInfo.getDependencies().get(0).getServiceDefinitionRequirement();
		if (providedPreconditionService.equalsIgnoreCase(requiredPreconditionService)) {
			throw new InvalidParameterException("precondition service missmatch");
		}
		
		final Integer preconditionServiceProvidedVersion = jobRequest.getPreconditionOrchestrationResults().get(0).getVersion();
		final int preconditionServiceRequiredMinVersion = serviceInfo.getDependencies().get(0).getMinVersionRequirement();
		final int preconditionServiceRequiredMaxVersion = serviceInfo.getDependencies().get(0).getMaxVersionRequirement();
		if (preconditionServiceProvidedVersion != null
				&& preconditionServiceProvidedVersion < preconditionServiceRequiredMinVersion
				&& preconditionServiceProvidedVersion > preconditionServiceRequiredMaxVersion) {
			throw new InvalidParameterException("precondition service version not supported");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void consumeCreateCarService() {
		final OrchestrationResultDTO orchResult = job.getJobRequest().getPreconditionOrchestrationResults().get(0); //We know we have only one dependency
		final Map<String, String> staticParameters = job.getJobRequest().getStaticParameters();
		final CarRequestDTO requestDTO = new CarRequestDTO(staticParameters.get(PARAMETER_BRAND), staticParameters.get(PARAMETER_COLOR));
		final CarResponseDTO response = driver.consumeCreateCarService(orchResult, requestDTO);
		printOut(response);
	}
	
	//-------------------------------------------------------------------------------------------------
	private void consumeGetCarService() {
		final OrchestrationResultDTO orchResult = job.getJobRequest().getMainOrchestrationResult();
		final Map<String, String> staticParameters = job.getJobRequest().getStaticParameters();
		final List<CarResponseDTO> response = driver.consumeGetCarService(orchResult, staticParameters.get(PARAMETER_BRAND), staticParameters.get(PARAMETER_COLOR));
		printOut(response);
	}
	
	//-------------------------------------------------------------------------------------------------
	private void notifyChoreographer(final ChoreographerExecutedStepStatus status) {
		notifyChoreographer(status, null, null);
	}
	
	//-------------------------------------------------------------------------------------------------
	private void notifyChoreographer(final ChoreographerExecutedStepStatus status, final String message, final String exception) {
		driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), status, message, exception);
	}
	
	//-------------------------------------------------------------------------------------------------
    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
