package ai.aitia.demo.robotic.arm.model.service.provided;

import org.springframework.http.HttpMethod;

public class RecognizeService {

	public static final String SERVICE_DEFINITION = "recognize";
	public static final String PATH = "/recognize";
	public static final String QUERY_PARAM_OBJECT = "object";
	public static final HttpMethod METHOD = HttpMethod.GET;
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private RecognizeService() {
		throw new UnsupportedOperationException();
	}
}
