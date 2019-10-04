package eu.arrowhead.client.skeleton.provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.client.skeleton.provider.security.ProviderSecurityConfig;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String mySystemName;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String mySystemAddress;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int mySystemPort;
	
	private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		if (tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		}		
		
		setTokenSecurityFilter();
		
		//Register services into ServiceRegistry
		final ServiceRegistryRequestDTO energyService = createServiceRegistryRequest(EFCommonConstants.ENERGY_FORECAST_SERVICE, EFCommonConstants.ENERGY_FORECAST_SERVICE_URI, HttpMethod.GET);
		energyService.getMetadata().put(EFCommonConstants.REQUEST_PARAM_KEY_BUILDING, EFCommonConstants.REQUEST_PARAM_BUILDING);
		energyService.getMetadata().put(EFCommonConstants.REQUEST_PARAM_KEY_TIMESTAMP, EFCommonConstants.REQUEST_PARAM_TIMESTAMP);
		arrowheadService.forceRegisterServiceToServiceRegistry(energyService);
		logger.info("Service registered: {}", EFCommonConstants.ENERGY_FORECAST_SERVICE);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		//Unregister service
		arrowheadService.unregisterServiceFromServiceRegistry(EFCommonConstants.ENERGY_FORECAST_SERVICE);
		logger.info("Service unregistered: {}", EFCommonConstants.ENERGY_FORECAST_SERVICE);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		if(!tokenSecurityFilterEnabled) {
			logger.info("TokenSecurityFilter in not active");
		} else {
			final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
			if (authorizationPublicKey == null) {
				throw new ArrowheadException("Authorization public key is null");
			}
			
			KeyStore keystore;
			try {
				keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
				keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				throw new ArrowheadException(ex.getMessage());
			}			
			final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

			providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
			providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition, final String serviceUri, final HttpMethod httpMethod) {
		final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
		serviceRegistryRequest.setServiceDefinition(serviceDefinition);
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);		
		systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));

		if (tokenSecurityFilterEnabled) {
			serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN);
			serviceRegistryRequest.setInterfaces(List.of(EFCommonConstants.INTERFACE_SECURE));
		} else if (sslEnabled) {
			serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE);
			serviceRegistryRequest.setInterfaces(List.of(EFCommonConstants.INTERFACE_SECURE));
		} else {
			serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE);
			serviceRegistryRequest.setInterfaces(List.of(EFCommonConstants.INTERFACE_INSECURE));
		}
		serviceRegistryRequest.setProviderSystem(systemRequest);
		serviceRegistryRequest.setServiceUri(serviceUri);
		serviceRegistryRequest.setMetadata(new HashMap<>());
		serviceRegistryRequest.getMetadata().put(EFCommonConstants.HTTP_METHOD, httpMethod.name());
		return serviceRegistryRequest;
	}
}
