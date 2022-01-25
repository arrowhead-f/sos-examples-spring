package eu.arrowhead.application.skeleton.executor.service;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.aitia.demo.object.loader.executor.execution.ExecutionBoard;
import ai.aitia.demo.object.loader.executor.model.service.LoadObjectService;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.ChoreographerAbortStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecuteStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoResponseDTO;
import eu.arrowhead.common.exception.BadPayloadException;

@Service
public class ExecutorService {

	//=================================================================================================
	// members
	
	@Autowired
	private ExecutionBoard executionBoard;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void startExecution(final ChoreographerExecuteStepRequestDTO request) {
		validateChoreographerExecuteStepRequestDTO(request);
		executionBoard.newJob(request);
	}
	
	//-------------------------------------------------------------------------------------------------
	public void abortExecution(final ChoreographerAbortStepRequestDTO request) {
		executionBoard.abortJob(request.getSessionId(), request.getSessionStepId());
	}
	
	//-------------------------------------------------------------------------------------------------
	public ChoreographerExecutorServiceInfoResponseDTO collectServiceInfo(final ChoreographerExecutorServiceInfoRequestDTO request) {
		validateChoreographerExecutorServiceInfoRequestDTO(request);
		
		final ChoreographerExecutorServiceInfoResponseDTO response = new ChoreographerExecutorServiceInfoResponseDTO();
		response.setServiceDefinition(LoadObjectService.SERVICE_DEFINITION);
		response.setMinVersion(LoadObjectService.VERSION);
		response.setMaxVersion(LoadObjectService.VERSION);
		return response;
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void validateChoreographerExecuteStepRequestDTO(final ChoreographerExecuteStepRequestDTO dto) {
		if (dto == null) {
			throw new BadPayloadException("dto is null", HttpStatus.SC_BAD_REQUEST);
		}		
		if (dto.getMainOrchestrationResult() == null) {
			throw new BadPayloadException("mainOrchestrationResult is null", HttpStatus.SC_BAD_REQUEST);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private void validateChoreographerExecutorServiceInfoRequestDTO(final ChoreographerExecutorServiceInfoRequestDTO dto) {
		if (dto == null) {
			throw new BadPayloadException("dto is null", HttpStatus.SC_BAD_REQUEST);
		}		
		if (Utilities.isEmpty(dto.getServiceDefinition())) {
			throw new BadPayloadException("serviceDefinition is empty", HttpStatus.SC_BAD_REQUEST);
		}
		if (dto.getMinVersion() == null) {
			throw new BadPayloadException("minVersion is empty", HttpStatus.SC_BAD_REQUEST);
		}
		if (dto.getMaxVersion() == null) {
			throw new BadPayloadException("maxVersion is empty", HttpStatus.SC_BAD_REQUEST);
		}
		if (dto.getMinVersion() > dto.getMaxVersion()) {
			throw new BadPayloadException("minVersion cannot be greater than maxVersion.", HttpStatus.SC_BAD_REQUEST);
		}
	}
}
