package ai.aitia.demo.car_consumer_with_subscribing;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;

import eu.arrowhead.client.skeleton.subscriber.security.SubscriberSecurityConfig;
import eu.arrowhead.client.skeleton.subscriber.ConfigEventProperites;
import eu.arrowhead.client.skeleton.subscriber.SubscriberUtilities;
import eu.arrowhead.client.skeleton.subscriber.constants.SubscriberConstants;

@Component
public class CarConsumerWithSubscriptionApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private SubscriberSecurityConfig subscriberSecurityConfig;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String clientSystemName;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String clientSystemAddress;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int clientSystemPort;
	
	private final Logger logger = LogManager.getLogger(CarConsumerWithSubscriptionApplicationInitListener.class);
	
	@Autowired
	private ConfigEventProperites configEventProperites;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Bean( SubscriberConstants.NOTIFICATION_QUEUE )
	public ConcurrentLinkedQueue<EventDTO> getNotificationQueue() {
		return new ConcurrentLinkedQueue<>();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Bean( SubscriberConstants.CONSUMER_TASK )
	public CarConsumerWithSubscriptionTask getConsumerTask() {
		return new CarConsumerWithSubscriptionTask();
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		}		
		
		setTokenSecurityFilter();
		
		setNotificationFilter();			

		
		if ( arrowheadService.echoCoreSystem( CoreSystem.EVENT_HANDLER ) ) {
			
			arrowheadService.updateCoreServiceURIs( CoreSystem.EVENT_HANDLER );	
			subscribeToPresetEvents();
			
		}
		
		checkCoreSystemReachability(CoreSystem.ORCHESTRATOR);		
		
		//Initialize Arrowhead Context
		arrowheadService.updateCoreServiceURIs(CoreSystem.ORCHESTRATOR);
		
		
		final CarConsumerWithSubscriptionTask consumerTask = applicationContext.getBean( SubscriberConstants.CONSUMER_TASK, CarConsumerWithSubscriptionTask.class );
		consumerTask.start();
		//TODO: implement here any custom behavior on application start up
	}


	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		 
		final Map<String, String> eventTypeMap = configEventProperites.getEventTypeURIMap();
		if( eventTypeMap == null) {
			
			logger.info("No preset events to unsubscribe.");
		
		} else {
			
			for ( final String eventType : eventTypeMap.keySet() ) {
				
				arrowheadService.unsubscribeFromEventHandler(eventType, clientSystemName, clientSystemAddress, clientSystemPort);
				
			}
		}
		
		if ( getConsumerTask() != null) {
			
			getConsumerTask().destroy();
		}
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		if(!tokenSecurityFilterEnabled || !sslEnabled) {
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
			} catch ( final KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				throw new ArrowheadException(ex.getMessage());
			}			
			final PrivateKey subscriberPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

			final Map<String, String> eventTypeMap = configEventProperites.getEventTypeURIMap();

			subscriberSecurityConfig.getTokenSecurityFilter().setEventTypeMap( eventTypeMap );
			subscriberSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
			subscriberSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(subscriberPrivateKey);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void subscribeToPresetEvents() {
		
		final Map<String, String> eventTypeMap = configEventProperites.getEventTypeURIMap();
		
		if( eventTypeMap == null) {
			
			logger.info("No preset events to subscribe.");
		
		} else {
			
			final SystemRequestDTO subscriber = new SystemRequestDTO();
			subscriber.setSystemName( clientSystemName );
			subscriber.setAddress( clientSystemAddress );
			subscriber.setPort( clientSystemPort );
			if (sslEnabled) {
				
				subscriber.setAuthenticationInfo( Base64.getEncoder().encodeToString( arrowheadService.getMyPublicKey().getEncoded()) );		
	
			}
			for (final String eventType  : eventTypeMap.keySet()) {
					
				try {
					
					arrowheadService.unsubscribeFromEventHandler(eventType, clientSystemName, clientSystemAddress, clientSystemPort);
				
				} catch (final Exception ex) {
					
					logger.debug("Exception happend in subscription initalization " + ex);
				}
				
				try {
					
					arrowheadService.subscribeToEventHandler( SubscriberUtilities.createSubscriptionRequestDTO( eventType, subscriber, eventTypeMap.get( eventType ) ) );
				
				} catch ( final InvalidParameterException ex) {
					
					if( ex.getMessage().contains( "Subscription violates uniqueConstraint rules" )) {
						
						logger.debug("Subscription is already in DB");
					
					} else {
						
						logger.debug(ex.getMessage());
						logger.debug(ex);
					}
					
					
				} catch ( final Exception ex) {
					
					logger.debug("Could not subscribe to EventType: " + eventType );
				} 
			}

		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private void setNotificationFilter() {
		logger.debug( "setNotificationFilter started..." );
		
		final Map<String, String> eventTypeMap = configEventProperites.getEventTypeURIMap();

		subscriberSecurityConfig.getNotificationFilter().setEventTypeMap( eventTypeMap );
		subscriberSecurityConfig.getNotificationFilter().setServerCN( arrowheadService.getServerCN() );
		
	}
}
