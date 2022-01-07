package ai.aitia.demo.robotic.arm.model.service.provided;

import org.springframework.http.HttpMethod;

public class DuckSeenService {

	public static final String SERVICE_DEFINITION = "duck-seen";
	public static final String PATH = "/duck";
	public static final HttpMethod METHOD = HttpMethod.GET;
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private DuckSeenService() {
		throw new UnsupportedOperationException();
	}
}
