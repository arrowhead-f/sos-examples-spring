package ai.aitia.demo.car_consumer;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import com.aitia.demo.car_common.dto.CarRequestDTO;
import com.aitia.demo.car_common.dto.CarResponseDTO;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, CarConsumerConstants.BASE_PACKAGE})
public class CarConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(CarConsumerMain.class);
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(CarConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
    	try {
    		createCarServiceOrchestrationAndConsumption();
    		getCarServiceOrchestrationAndConsumption();			
		} catch (final Exception ex) {
			logger.info("Proceedure 4.1.3 failed");
			logger.info(ex.getMessage());
		}
    	try {			
    		createCarServiceOrchestrationAndConsumptionLegacy();
    		getCarServiceOrchestrationAndConsumptionLegacy();
		} catch (final Exception ex) {
			logger.info("Proceedure 4.1.2 failed");
			logger.info(ex.getMessage());
		}
	}
    
    //-------------------------------------------------------------------------------------------------
    public void createCarServiceOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
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
			validateOrchestrationResult(orchestrationResult, CarConsumerConstants.CREATE_CAR_SERVICE_DEFINITION, getInterface());
			
			final List<CarRequestDTO> carsToCreate = List.of(new CarRequestDTO("nissan", "green"), new CarRequestDTO("mazda", "blue"), new CarRequestDTO("opel", "blue"), new CarRequestDTO("nissan", "gray"));
			
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
    }
    
    //-------------------------------------------------------------------------------------------------
    public void getCarServiceOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + CarConsumerConstants.GET_CAR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(CarConsumerConstants.GET_CAR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
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
			validateOrchestrationResult(orchestrationResult, CarConsumerConstants.GET_CAR_SERVICE_DEFINITION, getInterface());
			
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
    }
    
    //-------------------------------------------------------------------------------------------------
    public void createCarServiceOrchestrationAndConsumptionLegacy() {
    	logger.info("Orchestration request for legacy 'car' service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder("car")
    																		.interfaces("JSON")
    																		.build();
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   		 .flag(Flag.MATCHMAKING, true)
																					   		 .flag(Flag.OVERRIDE_STORE, true)
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
			validateOrchestrationResult(orchestrationResult, "car", "JSON");
			
			final CarRequestDTO carRequestDTO = new CarRequestDTO("ArrowMobile", "green");
			logger.info("Create a car request:");
			printOut(carRequestDTO);
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get("JSON");
			final String signature = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get("JSON-SIGNATURE");
			final String[] params = {"signature", signature};
			final CarRequestDTO carCreated = arrowheadService.consumeServiceHTTP(CarRequestDTO.class, HttpMethod.POST, orchestrationResult.getProvider().getAddress(),
																				  orchestrationResult.getProvider().getPort(), "/" + orchestrationResult.getServiceUri() + "/cars", 
																				  "HTTPS-JSON", token, carRequestDTO, Utilities.isEmpty(signature) ? new String[0] : params);
			logger.info("Provider response");
			printOut(carCreated);
		}
    }
    
    //-------------------------------------------------------------------------------------------------
    public void getCarServiceOrchestrationAndConsumptionLegacy() {
    	logger.info("Orchestration request for legacy 'car' service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder("car")
    																		.interfaces("JSON")
    																		.build();
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   		 .flag(Flag.MATCHMAKING, true)
																					   		 .flag(Flag.OVERRIDE_STORE, true)
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
			validateOrchestrationResult(orchestrationResult, "car", "JSON");
			
			final CarRequestDTO carRequestDTO = new CarRequestDTO("ArrowMobile", "green");
			logger.info("Get cars request:");
			printOut(carRequestDTO);
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get("JSON");
			final String signature = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get("JSON-SIGNATURE");
			final String[] params = {"signature", signature};
			@SuppressWarnings("unchecked")
			final List<CarRequestDTO> carCreated = arrowheadService.consumeServiceHTTP(List.class, HttpMethod.GET, orchestrationResult.getProvider().getAddress(),
																				  orchestrationResult.getProvider().getPort(), "/" + orchestrationResult.getServiceUri() + "/cars", 
																				  "HTTPS-JSON", token, carRequestDTO, Utilities.isEmpty(signature) ? new String[0] : params);
			logger.info("Provider response");
			printOut(carCreated);
		}
    }
    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? CarConsumerConstants.INTERFACE_SECURE : CarConsumerConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition, final String interfaceName) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}
    	
    	boolean hasValidInterface = false;
    	for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(interfaceName)) {
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
