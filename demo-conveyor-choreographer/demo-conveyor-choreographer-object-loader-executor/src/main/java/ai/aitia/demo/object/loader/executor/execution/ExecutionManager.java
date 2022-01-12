package ai.aitia.demo.object.loader.executor.execution;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.arrowhead.application.skeleton.executor.ExecutorConstants;

@Component
public class ExecutionManager extends Thread {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ExecutionBoard board;
	
	@Autowired
	private Function<Job,Runnable> workerFactory;
	
	private ThreadPoolExecutor threadPool;
	
	@Value(ExecutorConstants.$THREAD_NUM_EXECUTION_WORKER_WD)
	private int threadNum;

	private boolean doWork = true;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {	
		
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
		
		while (doWork) {
			try {				
				final Job job = board.nextJob();
				if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
					board.removeJob(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId());
					
				} else {
					threadPool.execute(workerFactory.apply(job));
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
