package ai.aitia.demo.conveyor.belt.model.service.provided;

import org.springframework.http.HttpMethod;

public class TransportWithConveyorService {
	
	public static final String SERVICE_DEFINITION = "transport-with-conveyor";
	public static final String PATH = "/move";
	public static final String QUERY_PARAM_SIGNED_DISTANCE = "signed-distance";
	public static final HttpMethod METHOD = HttpMethod.POST;
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private TransportWithConveyorService() {
		throw new UnsupportedOperationException();
	}
}
