package com.aitia.demo.car_provider;

public class CarProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "com.aitia";
	
	public static final String CAR_SERVICE_DEFINITION = "car-service";
	public static final String CAR_SERVICE_INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String CAR_SERVICE_INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String CAR_SERVICE_URI = "/car";
	
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private CarProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
