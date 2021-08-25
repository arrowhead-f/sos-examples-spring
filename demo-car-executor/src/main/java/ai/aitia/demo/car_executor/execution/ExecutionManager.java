package ai.aitia.demo.car_executor.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecutionManager extends Thread {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ExecutionBoard board;

	private boolean doWork = true;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {		
		while (doWork) {
			try {				
				final Job job = board.nextJob();
				if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
					board.removeJob(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId());
					
				} else {
					//TODO run job on new thread
				}
				
			} catch (final InterruptedException ex) {
				interrupt();
			}		
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void interrupt() {
		doWork = false;
		super.interrupt();
	}
}
