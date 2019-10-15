package ai.aitia.demo.energy_forecast.provider.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsListDTO;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class EnergyForecastDriver {

	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	protected SSLProperties sslProperties;
	
	private final Logger logger = LogManager.getLogger(EnergyForecastDriver.class);
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public EnergyDetailsListDTO getIndoorEnergyDetails(final long building, final long from, final long to) {
		final OrchestrationResultDTO orchestrationResult = orchestrate(EFCommonConstants.INDOOR_ENERGY_DETAILS_SERVICE);
		return consumeEnergyDetailsService(orchestrationResult, building, from, to);		
	}
	
	//-------------------------------------------------------------------------------------------------
	public EnergyDetailsListDTO getOutdoorEnergyDetails(final long building, final long from, final long to) {
		final OrchestrationResultDTO orchestrationResult = orchestrate(EFCommonConstants.OUTDOOR_ENERGY_DETAILS_SERVICE);
		return consumeEnergyDetailsService(orchestrationResult, building, from, to);
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
    private EnergyDetailsListDTO consumeEnergyDetailsService(final OrchestrationResultDTO orchestrationResult, final long building, final long from, final long to) {
    	final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		final String[] queryParam = {orchestrationResult.getMetadata().get(EFCommonConstants.REQUEST_PARAM_KEY_BUILDING), String.valueOf(building),
									 orchestrationResult.getMetadata().get(EFCommonConstants.REQUEST_PARAM_KEY_FROM), String.valueOf(from),
									 orchestrationResult.getMetadata().get(EFCommonConstants.REQUEST_PARAM_KEY_TO), String.valueOf(to)};
		
		return arrowheadService.consumeServiceHTTP(EnergyDetailsListDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(EFCommonConstants.HTTP_METHOD)),
												   orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
												   getInterface(), token, null, queryParam);
    }
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? EFCommonConstants.INTERFACE_SECURE : EFCommonConstants.INTERFACE_INSECURE;
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
}
