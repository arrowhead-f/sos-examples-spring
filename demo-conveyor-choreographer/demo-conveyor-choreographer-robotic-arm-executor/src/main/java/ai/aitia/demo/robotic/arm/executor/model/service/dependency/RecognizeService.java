package ai.aitia.demo.robotic.arm.executor.model.service.dependency;

public class RecognizeService {

	public static final String SERVICE_DEFINITION = "recognize";
	public static final String QUERY_PARAM_OBJECT = "object";
	public static final int VERSION = 1;
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private RecognizeService() {
		throw new UnsupportedOperationException();
	}
}
