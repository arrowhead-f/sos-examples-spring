package eu.arrowhead.application.skeleton.executor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.CoreServiceUri;
import ai.aitia.demo.car_common.dto.CarRequestDTO;
import ai.aitia.demo.car_common.dto.CarResponseDTO;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystemService;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepResultDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;

@Service
public class ExecutorDriver {

	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private SSLProperties sslProperties;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void notifyChoreographer(final long sessionId, final long sessionStepId, final ChoreographerExecutedStepStatus status,
									final String message, final String exception) {		
		Assert.notNull(status, "ChoreographerExecutedStepStatus is null");
		
		final ChoreographerExecutedStepResultDTO dto = new ChoreographerExecutedStepResultDTO();
		dto.setSessionId(sessionId);
		dto.setSessionStepId(sessionStepId);
		dto.setStatus(status);
		dto.setMessage(message);
		dto.setException(exception);
		
		final CoreServiceUri uri = arrowheadService.getCoreServiceUri(CoreSystemService.CHOREOGRAPHER_SERVICE);		
		arrowheadService.consumeServiceHTTP(Void.class, HttpMethod.POST, uri.getAddress(), uri.getPort(), uri.getPath(), getCoreSystemInterface(), null, dto, new String[0]);
	}
	
	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO consumeCreateCarService(final OrchestrationResultDTO orchResult, final CarRequestDTO request) {
		Assert.notNull(orchResult, "OrchestrationResultDTO is null");
		Assert.notNull(request, "CarRequestDTO is null");
		
		final String token = orchResult.getAuthorizationTokens() == null ? null : orchResult.getAuthorizationTokens().get(getInterface());
		return arrowheadService.consumeServiceHTTP(CarResponseDTO.class, HttpMethod.POST, orchResult.getProvider().getAddress(), orchResult.getProvider().getPort(), orchResult.getServiceUri(),
												   getInterface(), token, request, new String[0]);
	}
	
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<CarResponseDTO> consumeGetCarService(final OrchestrationResultDTO orchResult, final String brand, final String color) {
		Assert.notNull(orchResult, "OrchestrationResultDTO is null");
		Assert.isTrue(!Utilities.isEmpty(brand), "brand is empty");
		Assert.isTrue(!Utilities.isEmpty(color), "color is empty");
		
		final String[] queryParam = {"brand", brand, "color", color};	
		final String token = orchResult.getAuthorizationTokens() == null ? null : orchResult.getAuthorizationTokens().get(getInterface());
		return arrowheadService.consumeServiceHTTP(List.class, HttpMethod.POST, orchResult.getProvider().getAddress(), orchResult.getProvider().getPort(), orchResult.getServiceUri(),
				   								   getInterface(), token, null, queryParam);
	}
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	private String getCoreSystemInterface() {
		return sslProperties.isSslEnabled() ? CommonConstants.HTTP_SECURE_JSON : CommonConstants.HTTP_INSECURE_JSON;
	}
	
	//-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? CommonConstants.HTTP_SECURE_JSON : CommonConstants.HTTP_INSECURE_JSON;
    }
}
