package eu.arrowhead.application.skeleton.executor.execution.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.arrowhead.application.skeleton.executor.execution.Job;

public class UnkownServiceExecutionWorker implements Runnable {
	
	//=================================================================================================
	// members
	
	private final Job job;	
	
	private final Logger logger = LogManager.getLogger(UnkownServiceExecutionWorker.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public UnkownServiceExecutionWorker(Job job) {
		this.job = job;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		logger.error("Unkown service execution request: sessionId={}, sessionStepId={}, service={}",
					 job.getJobRequest().getSessionId(),
					 job.getJobRequest().getSessionStepId(),
					 job.getJobRequest().getMainOrchestrationResult().getService().getServiceDefinition());
	}
}
