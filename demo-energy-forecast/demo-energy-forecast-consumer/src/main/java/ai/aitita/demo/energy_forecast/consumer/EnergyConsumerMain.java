package ai.aitita.demo.energy_forecast.consumer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import ai.aitia.demo.energy.forecast.common.dto.EnergyForecastDTO;
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
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EFCommonConstants.BASE_PACKAGE})
public class EnergyConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(EnergyConsumerMain.class);
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(EnergyConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
    	OrchestrationResultDTO orchestrationResult = orchestrate(EFCommonConstants.ENERGY_FORECAST_SERVICE);
    	printOutJSON(orchestrationResult);
    	EnergyForecastDTO energyForecast = consumeEnergyForecastService(orchestrationResult, 10l, LocalDateTime.now().plusHours(1).toEpochSecond(ZoneOffset.UTC));
    	printOutXML(energyForecast);
    }
    
    //=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
    private OrchestrationResultDTO orchestrate(final String serviceDefinition) {
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(serviceDefinition)
    			.interfaces(getInterface())
    			.build();
    	
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
    	final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
    			.flag(Flag.MATCHMAKING, true)
    			.flag(Flag.OVERRIDE_STORE, true)
    			.build();
    	
    	final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
    	
    	if (orchestrationResponse == null) {
    		logger.info("No orchestration response received");
    	} else if (orchestrationResponse.getResponse().isEmpty()) {
    		logger.info("No provider found during the orchestration");
    	} else {
    		final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
    		validateOrchestrationResult(orchestrationResult, serviceDefinition);
    		return orchestrationResult;
    	}
    	throw new ArrowheadException("Unsuccessful orchestration: " + serviceDefinition);
    }
    
    //-------------------------------------------------------------------------------------------------
    private EnergyForecastDTO consumeEnergyForecastService(final OrchestrationResultDTO orchestrationResult, final long building, final long timestamp) {
    	final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		final String[] queryParam = {orchestrationResult.getMetadata().get(EFCommonConstants.REQUEST_PARAM_KEY_BUILDING), String.valueOf(building),
									 orchestrationResult.getMetadata().get(EFCommonConstants.REQUEST_PARAM_KEY_TIMESTAMP), String.valueOf(timestamp)};
		
		return arrowheadService.consumeServiceHTTP(EnergyForecastDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(EFCommonConstants.HTTP_METHOD)),
												   orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
												   getInterface(), token, null, queryParam);
    }
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? EFCommonConstants.INTERFACE_SECURE : EFCommonConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
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
    private void printOutJSON(final Object object) {
    	System.out.println("\n" + Utilities.toPrettyJson(Utilities.toJson(object)) + "\n");
    }
    
    //-------------------------------------------------------------------------------------------------
    private void printOutXML(final Object object) throws IOException {
    	XmlMapper xmlMapper = new XmlMapper();
    	xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    	System.out.println("\n" + xmlMapper.writeValueAsString(object) + "\n");
    }
}
