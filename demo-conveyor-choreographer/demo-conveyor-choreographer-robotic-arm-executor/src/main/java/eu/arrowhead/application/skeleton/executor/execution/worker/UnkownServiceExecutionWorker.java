package eu.arrowhead.application.skeleton.executor.execution.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.arrowhead.application.skeleton.executor.execution.Job;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;

public class UnkownServiceExecutionWorker implements Runnable {
	
	//=================================================================================================
	// members
	
	private final Job job;	
	
	@Autowired
	private ExecutorDriver driver;
	
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
		
		driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.ERROR,
				   				   job.getJobRequest().getMainOrchestrationResult().getService().getServiceDefinition() + " is not supported", null);
	}
}
