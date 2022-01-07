package eu.arrowhead.application.skeleton.executor;

public class ExecutorConstants {

	//=================================================================================================
	// members
	
	public static final String THREAD_NUM_EXECUTION_WORKER = "thread.num.execution-worker";
	public static final String $THREAD_NUM_EXECUTION_WORKER_WD = "${" + THREAD_NUM_EXECUTION_WORKER + ":1}";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ExecutorConstants() {
		throw new UnsupportedOperationException();
	}
}
