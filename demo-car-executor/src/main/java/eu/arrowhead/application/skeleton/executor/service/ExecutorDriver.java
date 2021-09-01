package eu.arrowhead.application.skeleton.executor.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import eu.arrowhead.common.dto.shared.ChoreographerExecutorRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

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
	public void registerExecutor(ChoreographerExecutorRequestDTO request) {
		Assert.notNull(request, "ChoreographerExecutorRequestDTO is null");
		
		final CoreServiceUri uri = arrowheadService.getCoreServiceUri(CoreSystemService.CHOREOGRAPHER_REGISTER_EXECUTOR_SERVICE);
		arrowheadService.consumeServiceHTTP(ChoreographerExecutorResponseDTO.class, HttpMethod.POST, uri.getAddress(), uri.getPort(), uri.getPath(), getCoreSystemInterface(), null, request, new String[0]);
	}
	
	//-------------------------------------------------------------------------------------------------
	public void unregisterExecutor(final String executorName) {
		Assert.isTrue(!Utilities.isEmpty(executorName), "executorName is empty");		
		
		final String[] queryParam = {CommonConstants.OP_CHOREOGRAPHER_EXECUTOR_UNREGISTER_REQUEST_PARAM_NAME, executorName};
		final CoreServiceUri uri = arrowheadService.getCoreServiceUri(CoreSystemService.CHOREOGRAPHER_UNREGISTER_EXECUTOR_SERVICE);
		arrowheadService.consumeServiceHTTP(Void.class, HttpMethod.DELETE, uri.getAddress(), uri.getPort(), uri.getPath(), getCoreSystemInterface(), null, null, queryParam);
	}

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
		
		final String token = orchResult.getAuthorizationTokens() == null ? null : orchResult.getAuthorizationTokens().get(getInterface(orchResult));
		return arrowheadService.consumeServiceHTTP(CarResponseDTO.class, HttpMethod.POST, orchResult.getProvider().getAddress(), orchResult.getProvider().getPort(), orchResult.getServiceUri(),
												   getInterface(orchResult), token, request, new String[0]);
	}
	
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<CarResponseDTO> consumeGetCarService(final OrchestrationResultDTO orchResult, final String brand, final String color) {
		Assert.notNull(orchResult, "OrchestrationResultDTO is null");
		Assert.isTrue(!Utilities.isEmpty(brand), "brand is empty");
		Assert.isTrue(!Utilities.isEmpty(color), "color is empty");
		
		final String[] queryParam = {"brand", brand, "color", color};	
		final String token = orchResult.getAuthorizationTokens() == null ? null : orchResult.getAuthorizationTokens().get(getInterface(orchResult));
		return arrowheadService.consumeServiceHTTP(List.class, HttpMethod.POST, orchResult.getProvider().getAddress(), orchResult.getProvider().getPort(), orchResult.getServiceUri(),
				   								   getInterface(orchResult), token, null, queryParam);
	}
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	private String getCoreSystemInterface() {
		return sslProperties.isSslEnabled() ? CommonConstants.HTTP_SECURE_JSON : CommonConstants.HTTP_INSECURE_JSON;
	}
	
	//-------------------------------------------------------------------------------------------------
    private String getInterface(final OrchestrationResultDTO orchResult) {
    	final Set<String> interfaces = new HashSet<>();
    	for (final ServiceInterfaceResponseDTO interfaceDTO : orchResult.getInterfaces()) {
    		interfaces.add(interfaceDTO.getInterfaceName());
    	}
    	
    	if (!sslProperties.isSslEnabled() && interfaces.contains(CommonConstants.HTTP_INSECURE_JSON)) {
    		return CommonConstants.HTTP_INSECURE_JSON;
    		
		}
    	
    	if (sslProperties.isSslEnabled()) {
			if (interfaces.contains(CommonConstants.HTTP_SECURE_JSON)) {
				return CommonConstants.HTTP_SECURE_JSON;
			}
			if (interfaces.contains(CommonConstants.HTTP_INSECURE_JSON)) {
				return CommonConstants.HTTP_INSECURE_JSON;
			}
		}
    	
    	throw new InvalidParameterException("Have no proper interface to call the provider.");
    }
}
