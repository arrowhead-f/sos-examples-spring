package com.aitia.demo.car_consumer.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import eu.arrowhead.client.skeleton.common.ArrowheadService;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;

public class ServiceModel<T> {
	
	//=================================================================================================
	// members
	
	private final ArrowheadService arrowheadService;
	
	private final String serviceAddress;
	private final int servicePort;
	private final String serviceUri;
	private final String interfaceName;
	private final ServiceSecurityType securityType;
	private final String token;	
	private final Class<T> responseType;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public ServiceModel(final ArrowheadService arrowheadService, final OrchestrationResultDTO orchestrationResult, final String requestedInterfaceName, final Class<T> responseType) {		
		this.arrowheadService = arrowheadService;
		this.serviceAddress = orchestrationResult.getProvider().getAddress();
		this.servicePort = orchestrationResult.getProvider().getPort();
		this.serviceUri = orchestrationResult.getServiceUri();
		this.interfaceName = requestedInterfaceName;
		this.securityType = orchestrationResult.getSecure();
		this.token = orchestrationResult.getAuthorizationTokens().get(interfaceName);
		this.responseType = responseType;
	}
		
	//-------------------------------------------------------------------------------------------------
	public String getServiceAddress() {	return serviceAddress; }
	public int getServicePort() { return servicePort; }
	public String getServiceUri() { return serviceUri; }
	public String getInterfaceName() { return interfaceName; }
	public ServiceSecurityType getSecurityType() { return securityType; }
	public String getToken() { return token; }
	public Class<T> getResponseType() { return responseType; }

	//-------------------------------------------------------------------------------------------------
	public T consumeHTTP(final HttpMethod httpMethod, final Object payload, final String... queryParams) {
		return arrowheadService.consumeServiceHTTP(responseType, httpMethod, serviceAddress, servicePort, serviceUri, interfaceName, token, payload, queryParams);
	}
	

}
