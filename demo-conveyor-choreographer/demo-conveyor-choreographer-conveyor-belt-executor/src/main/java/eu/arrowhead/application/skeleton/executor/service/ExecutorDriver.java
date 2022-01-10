package eu.arrowhead.application.skeleton.executor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.CoreServiceUri;
import ai.aitia.demo.conveyor.belt.executor.model.service.TransportWithConveyorService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.core.CoreSystemService;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepResultDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorResponseDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
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
	public void forceRegisterIntoChoreographer(final String systemName, final String address, final int port, final String authenticationInfo) {
		final ChoreographerExecutorRequestDTO dto = new ChoreographerExecutorRequestDTO();
		dto.setServiceDefinitionName(TransportWithConveyorService.SERVICE_DEFINITION);
		dto.setMinVersion(TransportWithConveyorService.VERSION);
		dto.setMaxVersion(TransportWithConveyorService.VERSION);
		dto.setSystem(new SystemRequestDTO(systemName, address, port, authenticationInfo, null));
		
		final CoreServiceUri regUri = arrowheadService.getCoreServiceUri(CoreSystemService.CHOREOGRAPHER_REGISTER_EXECUTOR_SERVICE);
		try {
			arrowheadService.consumeServiceHTTP(ChoreographerExecutorResponseDTO.class, HttpMethod.POST, regUri.getAddress(), regUri.getPort(), regUri.getPath(), getCoreSystemInterface(), null, dto, new String[0]);
			
		} catch (final InvalidParameterException ex) {
			final CoreServiceUri unregUri = arrowheadService.getCoreServiceUri(CoreSystemService.CHOREOGRAPHER_UNREGISTER_EXECUTOR_SERVICE);
			final String[] params = {CommonConstants.OP_CHOREOGRAPHER_EXECUTOR_UNREGISTER_REQUEST_PARAM_NAME, systemName};
			arrowheadService.consumeServiceHTTP(Void.class, HttpMethod.DELETE, unregUri.getAddress(), unregUri.getPort(), unregUri.getPath(), getCoreSystemInterface(), null, null, params);
			arrowheadService.consumeServiceHTTP(ChoreographerExecutorResponseDTO.class, HttpMethod.POST, regUri.getAddress(), regUri.getPort(), regUri.getPath(), getCoreSystemInterface(), null, dto, new String[0]);
		}
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
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	private String getCoreSystemInterface() {
		return sslProperties.isSslEnabled() ? CommonConstants.HTTP_SECURE_JSON : CommonConstants.HTTP_INSECURE_JSON;
	}
}
