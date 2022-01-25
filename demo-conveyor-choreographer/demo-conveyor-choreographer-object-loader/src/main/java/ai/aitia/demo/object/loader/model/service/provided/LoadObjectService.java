package ai.aitia.demo.object.loader.model.service.provided;

import org.springframework.http.HttpMethod;

public class LoadObjectService {
	
	public static final String SERVICE_DEFINITION = "load-object";
	public static final String PATH = "/load";
	public static final HttpMethod METHOD = HttpMethod.POST;
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private LoadObjectService() {
		throw new UnsupportedOperationException();
	}
}
