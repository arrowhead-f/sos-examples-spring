package ai.aitia.demo.energy.forecast.common;


public class EFCommonConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String ENERGY_FORECAST_SERVICE = "energy-forecast-details";
	public static final String ENERGY_FORECAST_SERVICE_URI = "/energy";
	
	public static final String OUTDOOR_ENERGY_DETAILS_SERVICE = "outdoor-energy-details";
	public static final String OUTDOOR_ENERGY_DETAILS_SERVICE_URI = "/outdoor";
	
	public static final String INDOOR_ENERGY_DETAILS_SERVICE = "indoor-energy-details";
	public static final String INDOOR_ENERGY_DETAILS_SERVICE_URI = "/indoor";
	
	public static final String REQUEST_PARAM_KEY_BUILDING = "request-param-building";
	public static final String REQUEST_PARAM_BUILDING = "building";
	public static final String REQUEST_PARAM_KEY_FROM = "request-param-from";
	public static final String REQUEST_PARAM_FROM = "from";
	public static final String REQUEST_PARAM_KEY_TO = "request-param-to";
	public static final String REQUEST_PARAM_TO = "to";
	public static final String REQUEST_PARAM_KEY_TIMESTAMP = "request-param-timestamp";
	public static final String REQUEST_PARAM_TIMESTAMP = "timestamp";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-XML";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-XML";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private EFCommonConstants() {
		throw new UnsupportedOperationException();
	}
}
