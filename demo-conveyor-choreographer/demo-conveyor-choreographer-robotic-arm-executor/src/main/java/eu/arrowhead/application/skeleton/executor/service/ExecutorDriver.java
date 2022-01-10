package eu.arrowhead.application.skeleton.executor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.CoreServiceUri;
import ai.aitia.demo.robotic.arm.executor.model.service.TakeOffService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.core.CoreSystemService;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepResultDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorResponseDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;

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
	public void registerIntoChoreographer(final String systemName, final String address, final int port, final String authenticationInfo) {
		final ChoreographerExecutorRequestDTO dto = new ChoreographerExecutorRequestDTO();
		dto.setServiceDefinitionName(TakeOffService.SERVICE_DEFINITION);
		dto.setMinVersion(TakeOffService.VERSION);
		dto.setMaxVersion(TakeOffService.VERSION);
		dto.setSystem(new SystemRequestDTO(systemName, address, port, authenticationInfo, null));
		
		final CoreServiceUri uri = arrowheadService.getCoreServiceUri(CoreSystemService.CHOREOGRAPHER_REGISTER_EXECUTOR_SERVICE);
		arrowheadService.consumeServiceHTTP(ChoreographerExecutorResponseDTO.class, HttpMethod.POST, uri.getAddress(), uri.getPort(), uri.getPath(), getCoreSystemInterface(), null, dto, new String[0]);
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
