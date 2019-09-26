package com.aitia.demo.car_consumer;

public class CarConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "com.aitia";
	
	public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	
	public static final String CAR_SERVICE_DEFINITION = "car-service";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private CarConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
