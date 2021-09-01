package eu.arrowhead.application.skeleton.executor;

public class ExecutorConstants {

	//=================================================================================================
	// members
	
	public static final String THREAD_NUM_EXECUTION_WORKER = "thread.num.execution-worker";
	public static final String $THREAD_NUM_EXECUTION_WORKER_WD = "${" + THREAD_NUM_EXECUTION_WORKER + ":1}";
	
	public static final String BASE_PACKAGE = "ai.aitia";
	public static final String BASE_URI = "/executor";
	public static final String MAIN_SERVICE_GET_CAR = "get-car";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ExecutorConstants() {
		throw new UnsupportedOperationException();
	}
}
