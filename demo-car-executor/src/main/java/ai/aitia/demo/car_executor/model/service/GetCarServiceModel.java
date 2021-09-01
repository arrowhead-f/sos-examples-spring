package ai.aitia.demo.car_executor.model.service;

import java.util.List;

import org.springframework.stereotype.Component;

import eu.arrowhead.application.skeleton.executor.ExecutorConstants;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;

@Component
public class GetCarServiceModel {

	//=================================================================================================
	// members
	
	private List<ServiceQueryFormDTO> dependencies;
	private boolean initialized = false;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public boolean isServable(final ChoreographerExecutorServiceInfoRequestDTO request) {
		return request.getServiceDefinition().equalsIgnoreCase(ExecutorConstants.MAIN_SERVICE_GET_CAR)
				&& request.getMinVersion() >= ExecutorConstants.MAIN_SERVICE_GET_CAR_MIN_VERSION
				&& request.getMaxVersion() <= ExecutorConstants.MAIN_SERVICE_GET_CAR_MAX_VERSION;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ChoreographerExecutorServiceInfoResponseDTO createChoreographerExecutorServiceInfoResponseDTO() {
		if (!initialized) {
			initialize();
		}
		
		return new ChoreographerExecutorServiceInfoResponseDTO(ExecutorConstants.MAIN_SERVICE_GET_CAR, ExecutorConstants.MAIN_SERVICE_GET_CAR_MIN_VERSION,
															   ExecutorConstants.MAIN_SERVICE_GET_CAR_MAX_VERSION, dependencies);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void initialize() {
		dependencies = List.of(createCarService());
		initialized = true;
	}
	
	//-------------------------------------------------------------------------------------------------
	private ServiceQueryFormDTO createCarService() {
		final ServiceQueryFormDTO form = new ServiceQueryFormDTO();
		form.setServiceDefinitionRequirement(ExecutorConstants.PRECONDITION_SERVICE_CREATE_CAR);
		return form;
	}
}
