package ai.aitia.demo.energy.forecast.common;


public class EnergyForecastCommonConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String OUTDOOR_TEMPERATURE_SERVICE = "outdoor-temperature";
	
	public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private EnergyForecastCommonConstants() {
		throw new UnsupportedOperationException();
	}
}
