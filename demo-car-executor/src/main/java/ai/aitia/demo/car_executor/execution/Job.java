package ai.aitia.demo.car_executor.execution;

import eu.arrowhead.common.dto.shared.ChoreographerExecuteStepRequestDTO;

public class Job {
	
	//=================================================================================================
	// members
	
	private final ChoreographerExecuteStepRequestDTO jobRequest;
	private ExecutionSignal executionSignal;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Job(final ChoreographerExecuteStepRequestDTO jobRequest, final ExecutionSignal executionSignal) {
		this.jobRequest = jobRequest;
		this.executionSignal = executionSignal;
	}

	//-------------------------------------------------------------------------------------------------
	public ExecutionSignal getExecutionSignal() { return executionSignal; }
	public ChoreographerExecuteStepRequestDTO getJobRequest() { return jobRequest; }

	//-------------------------------------------------------------------------------------------------
	public void setExecutionSignal(final ExecutionSignal executionSignal) { this.executionSignal = executionSignal; }
}
