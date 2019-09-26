package com.aitia.demo.car_consumer;

public class CarConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "com.aitia";
	
	public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String CREATE_CAR_SERVICE_DEFINITION = "create-car";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private CarConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
