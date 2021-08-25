package eu.arrowhead.application.skeleton.executor.service;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.ChoreographerAbortStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecuteStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoResponseDTO;
import eu.arrowhead.common.exception.BadPayloadException;

@Service
public class ExecutorService {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void startExecution(final ChoreographerExecuteStepRequestDTO request) {
		validateChoreographerExecuteStepRequestDTO(request);
		//TODO implement your logic here
	}
	
	//-------------------------------------------------------------------------------------------------
	public void abortExecution(final ChoreographerAbortStepRequestDTO request) {
		//TODO implement your logic here 
	}
	
	//-------------------------------------------------------------------------------------------------
	public ChoreographerExecutorServiceInfoResponseDTO collectServiceInfo(final ChoreographerExecutorServiceInfoRequestDTO request) {
		validateChoreographerExecutorServiceInfoRequestDTO(request);
		
		final ChoreographerExecutorServiceInfoResponseDTO response = new ChoreographerExecutorServiceInfoResponseDTO();
		//TODO implement your logic here 
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
