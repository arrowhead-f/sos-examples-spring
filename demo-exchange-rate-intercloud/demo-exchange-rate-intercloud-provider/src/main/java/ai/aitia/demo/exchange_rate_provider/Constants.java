package ai.aitia.demo.exchange_rate_provider;

public class Constants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String GET_EXCHANGE_RATE_SERVICE_DEFINITION = "exchange-rate";
	public static final String GET_EXCHANGE_RATE_SERVICE_URI = "/exchange_rate";
	
	public static final String REQUEST_PARAM_META_CURRENCY_RELATION = "request-param-currency-relation";
	public static final String REQUEST_PARAM_CURRENCY_RELATION = "currency-relation";
	public static final String REQUEST_PARAM_META_HUF_EUR_VALUE = "request-value-huf-eur";
	public static final String REQUEST_PARAM_HUF_EUR_VALUE = "HUF-EUR";
	public static final String REQUEST_PARAM_META_EUR_HUF_VALUE = "request-value-eur-huf";
	public static final String REQUEST_PARAM_EUR_HUF_VALUE = "EUR-HUF";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Constants() {
		throw new UnsupportedOperationException();
	}
}
