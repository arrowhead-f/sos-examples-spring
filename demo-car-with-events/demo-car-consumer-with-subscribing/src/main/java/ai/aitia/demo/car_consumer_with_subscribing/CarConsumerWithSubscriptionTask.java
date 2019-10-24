package ai.aitia.demo.car_consumer_with_subscribing;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import com.aitia.demo.car_common.dto.CarRequestDTO;
import com.aitia.demo.car_common.dto.CarResponseDTO;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.client.skeleton.subscriber.SubscriberUtilities;
import eu.arrowhead.client.skeleton.subscriber.constants.SubscriberConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.dto.shared.SubscriptionRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.dto.shared.SystemResponseDTO;
import eu.arrowhead.common.exception.InvalidParameterException;


public class CarConsumerWithSubscriptionTask extends Thread {
	//=================================================================================================
	// members
	
	private boolean interrupted = false;
	
	private final Logger logger = LogManager.getLogger(CarConsumerWithSubscriptionTask.class);
	
	@Resource( name = SubscriberConstants.NOTIFICATION_QUEUE )
	private ConcurrentLinkedQueue<EventDTO> notificatonQueue;
	
	final Set<String> receivedEvenTypeList = new HashSet<>();
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
	
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
	
	@Value(CarConsumerConstants.$REORCHESTRATION_WD)
	private boolean reorchestration;
	
	@Value(CarConsumerConstants.$MAX_RETRY_WD)
	private int max_retry;

	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@Override
	public void run() {
		logger.info("ConsumerTask.run started...");
		
		interrupted = Thread.currentThread().isInterrupted();

		OrchestrationResultDTO carCreationService = null;
		OrchestrationResultDTO carRequestingService = null;
		
		int counter = 0;
		while ( !interrupted && ( counter < max_retry ) ) {
			
			try {
				
				if ( !( notificatonQueue.peek() == null) ) {
					
					for (final EventDTO event : notificatonQueue ) {
						
						if ( SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE.equalsIgnoreCase( event.getEventType() )) {
							
							if ( reorchestration ) {
								
								logger.info("Recieved publisher destroyed event - started reorchestration.");
								
								carCreationService = orchestrateCreateCarService();
								carRequestingService = orchestrateGetCarService();
								
							} else {
								
								logger.info("Recieved publisher destroyed event - started shuting down.");
								
								System.exit( 0 );
								
							}
							
						}else {
							
							logger.info("ConsumerTask recevied event - with type: " + event.getEventType() + ", and payload: " + event.getPayload() + ".");
							
						}
						
					}
					
					notificatonQueue.clear();
					
				}
					
				if ( carCreationService != null  && carRequestingService != null ) {
													
					final List<CarRequestDTO> carsToCreate = List.of(new CarRequestDTO("nissan", "green"), new CarRequestDTO("mazda", "blue"), new CarRequestDTO("opel", "blue"), new CarRequestDTO("nissan", "gray"));
			    	
					callCarCreationService( carCreationService , carsToCreate);
					callCarRequestingService( carRequestingService );

				} else {
					
					counter++;
					
					carCreationService = orchestrateCreateCarService();
					carRequestingService = orchestrateGetCarService();
					
					if ( carCreationService != null  && carRequestingService != null ) {
						
						counter = 0;
						
						final Set<SystemResponseDTO> sources = new HashSet<SystemResponseDTO>();
						
						sources.add( carCreationService.getProvider() );
						sources.add( carRequestingService.getProvider() );
						
						subscribeToDestoryEvents( sources );
					}
				}
				
			} catch ( final Throwable ex ) {
				
				logger.debug( ex.getMessage() );
				
				carCreationService = null;
				carRequestingService = null;
			}	

		}
		
		System.exit( 0 );

	}
	
	//-------------------------------------------------------------------------------------------------	
	public void destroy() {
		logger.debug("ConsumerTask.destroy started...");
		
		interrupted = true;
	}
	
	//=================================================================================================
	//Assistant methods

    //-------------------------------------------------------------------------------------------------
    private void callCarCreationService( final OrchestrationResultDTO orchestrationResult, final List<CarRequestDTO> carsToCreate) {
    	logger.debug("consumeCreateCarService started...");
    	
		validateOrchestrationResult(orchestrationResult, CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION);
		
			
		for (final CarRequestDTO carRequestDTO : carsToCreate) {
			logger.info("Create a car request:");
			printOut(carRequestDTO);
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			final CarResponseDTO carCreated = arrowheadService.consumeServiceHTTP(CarResponseDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(CarConsumerConstants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
					getInterface(), token, carRequestDTO, new String[0]);
			logger.info("Provider response");
			printOut(carCreated);
		}			
    }
	
	//-------------------------------------------------------------------------------------------------
	private void subscribeToDestoryEvents( final Set<SystemResponseDTO> providers  ) {

		final Set<SystemRequestDTO> sources = new HashSet<>(providers.size());
		
		for (final SystemResponseDTO provider : providers) {
			
			final SystemRequestDTO source = new SystemRequestDTO();
			source.setSystemName( provider.getSystemName() );
			source.setAddress( provider.getAddress() );
			source.setPort( provider.getPort() );
			
			sources.add( source );
		}
		
		final SystemRequestDTO subscriber = new SystemRequestDTO();
		subscriber.setSystemName( clientSystemName );
		subscriber.setAddress( clientSystemAddress );
		subscriber.setPort( clientSystemPort );
		
		if (sslEnabled) {
			
			subscriber.setAuthenticationInfo( Base64.getEncoder().encodeToString( arrowheadService.getMyPublicKey().getEncoded()) );		
		
		}
		
		try {
			
			arrowheadService.unsubscribeFromEventHandler( SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE, clientSystemName, clientSystemAddress, clientSystemPort);
		
		} catch (final Exception ex) {
			
			logger.debug("Exception happend in subscription initalization " + ex);
		}
		
		try {
			
			final SubscriptionRequestDTO subscription = SubscriberUtilities.createSubscriptionRequestDTO( SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE, subscriber, SubscriberConstants.PUBLISHER_DESTORYED_NOTIFICATION_URI );
			subscription.setSources( sources );
			
			arrowheadService.subscribeToEventHandler( subscription );
		
		} catch ( final InvalidParameterException ex) {
			
			if( ex.getMessage().contains( "Subscription violates uniqueConstraint rules" )) {
				
				logger.debug("Subscription is already in DB");
			}
			
		} catch ( final Exception ex) {
			
			logger.debug("Could not subscribe to EventType: " + SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE );
		}

	}
	
	//-------------------------------------------------------------------------------------------------
    private OrchestrationResultDTO orchestrateCreateCarService() {
    	logger.info("Orchestration request for " + CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, false)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		
		printOut(orchestrationFormRequest);		
		
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		logger.info("Orchestration response:");
		printOut(orchestrationResponse);	
		
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION);
			
			return orchestrationResult;
			
		}
		
		return null;
    }
    
    //-------------------------------------------------------------------------------------------------
    private OrchestrationResultDTO orchestrateGetCarService() {
    	logger.info("Orchestration request for " + CarConsumerConstants.GET_CAR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(CarConsumerConstants.GET_CAR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, false)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		
		printOut(orchestrationFormRequest);		
		
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		
		logger.info("Orchestration response:");
		printOut(orchestrationResponse);		
		
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, CarConsumerConstants.GET_CAR_SERVICE_DEFINITION);
			
			return orchestrationResult;
		}
		
		return null;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void callCarRequestingService( final OrchestrationResultDTO orchestrationResult) {
    			
		validateOrchestrationResult(orchestrationResult, CarConsumerConstants.GET_CAR_SERVICE_DEFINITION);
		
		logger.info("Get all cars:");
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		@SuppressWarnings("unchecked")
		final List<CarResponseDTO> allCar = arrowheadService.consumeServiceHTTP(List.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(CarConsumerConstants.HTTP_METHOD)),
																				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
																				getInterface(), token, null, new String[0]);
		printOut(allCar);
		
		logger.info("Get only blue cars:");
		final String[] queryParamColor = {orchestrationResult.getMetadata().get(CarConsumerConstants.REQUEST_PARAM_KEY_COLOR), "blue"};			
		@SuppressWarnings("unchecked")
		final List<CarResponseDTO> blueCars = arrowheadService.consumeServiceHTTP(List.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(CarConsumerConstants.HTTP_METHOD)),
																				  orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
																				  getInterface(), token, null, queryParamColor);
		printOut(blueCars);
		
    }
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? CarConsumerConstants.INTERFACE_SECURE : CarConsumerConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinitin) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinitin)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}
    	
    	boolean hasValidInterface = false;
    	for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
				hasValidInterface = true;
				break;
			}
		}
    	if (!hasValidInterface) {
    		throw new InvalidParameterException("Requested and orchestrated interface do not match");
		}
    }
    
    //-------------------------------------------------------------------------------------------------
    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
