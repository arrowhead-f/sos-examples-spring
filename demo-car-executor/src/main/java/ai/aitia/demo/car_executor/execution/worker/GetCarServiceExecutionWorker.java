package ai.aitia.demo.car_executor.execution.worker;

import org.springframework.beans.factory.annotation.Autowired;

import ai.aitia.demo.car_executor.execution.ExecutionBoard;
import ai.aitia.demo.car_executor.execution.ExecutionSignal;
import ai.aitia.demo.car_executor.execution.Job;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;

public class GetCarServiceExecutionWorker implements Runnable {

	//=================================================================================================
	// members
	
	private final Job job;
	
	@Autowired
	private ExecutionBoard board;
	
	private ExecutorDriver driver;
	
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
			notifyChoreographer(ChoreographerExecutedStepStatus.ERROR, ex.getMessage(), ex.getClass().getSimpleName());
		}
		
		if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
			board.removeJob(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId());
			return;
		}
		
		try {
			consumeGetCarService();			
		} catch (final Exception ex) {
			notifyChoreographer(ChoreographerExecutedStepStatus.ERROR, ex.getMessage(), ex.getClass().getSimpleName());
		}
		
		notifyChoreographer(ChoreographerExecutedStepStatus.SUCCESS);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void consumeCreateCarService() {
		final OrchestrationResultDTO orchResult = job.getJobRequest().getPreconditionOrchestrationResults().get(0);
	}
	
	//-------------------------------------------------------------------------------------------------
	private void consumeGetCarService() {
		final OrchestrationResultDTO orchResult = job.getJobRequest().getMainOrchestrationResult();
	}
	
	//-------------------------------------------------------------------------------------------------
	private void notifyChoreographer(final ChoreographerExecutedStepStatus status) {
		notifyChoreographer(status, null, null);
	}
	
	//-------------------------------------------------------------------------------------------------
	private void notifyChoreographer(final ChoreographerExecutedStepStatus status, final String message, final String exception) {
		driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), status, message, exception);
	}
}
