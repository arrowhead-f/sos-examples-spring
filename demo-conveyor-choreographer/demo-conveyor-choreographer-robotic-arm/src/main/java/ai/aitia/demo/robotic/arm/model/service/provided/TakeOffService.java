package ai.aitia.demo.robotic.arm.model.service.provided;

import org.springframework.http.HttpMethod;

public class TakeOffService {

	public static final String SERVICE_DEFINITION = "take-off";
	public static final String PATH = "/take-off";
	public static final HttpMethod METHOD = HttpMethod.POST;
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private TakeOffService() {
		throw new UnsupportedOperationException();
	}
}
