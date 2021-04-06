package ai.aitia.demo.car_consumer_with_subscribing;

public class CarConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String CREATE_CAR_SERVICE_DEFINITION = "create-car";
	public static final String GET_CAR_SERVICE_DEFINITION = "get-car";
	public static final String REQUEST_PARAM_KEY_BRAND = "request-param-brand";
	public static final String REQUEST_PARAM_KEY_COLOR = "request-param-color";
	public static final String $REORCHESTRATION_WD = "${reorchestration:false}";
	public static final String $MAX_RETRY_WD = "${max_retry:300}";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private CarConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
