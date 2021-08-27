package eu.arrowhead.application.skeleton.executor.execution.worker;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.demo.car_common.dto.CarRequestDTO;
import ai.aitia.demo.car_common.dto.CarResponseDTO;
import eu.arrowhead.application.skeleton.executor.execution.ExecutionBoard;
import eu.arrowhead.application.skeleton.executor.execution.ExecutionSignal;
import eu.arrowhead.application.skeleton.executor.execution.Job;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;

public class GetCarServiceExecutionWorker implements Runnable {

	//=================================================================================================
	// members
	
	private final Job job;
	
	@Autowired
	private ExecutionBoard board;
	
	@Autowired
	private ExecutorDriver driver;
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	private final Logger logger = LogManager.getLogger(GetCarServiceExecutionWorker.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public GetCarServiceExecutionWorker(final Job job) {
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
			consumeCreateCarService();			
		} catch (final Exception ex) {
			logger.debug(ex);
			notifyChoreographer(ChoreographerExecutedStepStatus.ERROR, ex.getMessage(), ex.getClass().getSimpleName());
		}
		
		if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
			board.removeJob(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId());
			return;
		}
		
		try {
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
	private void consumeCreateCarService() {
		final OrchestrationResultDTO orchResult = job.getJobRequest().getPreconditionOrchestrationResults().get(0); //We know we have only one dependency
		final Map<String, String> staticParameters = job.getJobRequest().getStaticParameters();
		final CarRequestDTO requestDTO = new CarRequestDTO(staticParameters.get("brand"), staticParameters.get("color"));
		final CarResponseDTO response = driver.consumeCreateCarService(orchResult, requestDTO);
		printOut(response);
	}
	
	//-------------------------------------------------------------------------------------------------
	private void consumeGetCarService() {
		final OrchestrationResultDTO orchResult = job.getJobRequest().getMainOrchestrationResult();
		final Map<String, String> staticParameters = job.getJobRequest().getStaticParameters();
		final List<CarResponseDTO> response = driver.consumeGetCarService(orchResult, staticParameters.get("brand"), staticParameters.get("color"));
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
