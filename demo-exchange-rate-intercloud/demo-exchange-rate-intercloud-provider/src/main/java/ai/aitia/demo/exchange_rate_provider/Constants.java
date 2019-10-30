package ai.aitia.demo.exchange_rate_provider;

public class Constants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String GET_EXCHANGE_RATE_SERVICE_DEFINITION = "exchange-rate";
	public static final String GET_EXCHANGE_RATE_SERVICE_URI = "/exchange_rate";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Constants() {
		throw new UnsupportedOperationException();
	}
}
