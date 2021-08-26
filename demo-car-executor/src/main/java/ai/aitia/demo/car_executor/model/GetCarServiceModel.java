package ai.aitia.demo.car_executor.model;

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
	
	private static final String MAIN_SERVICE_DEFINITION = ExecutorConstants.MAIN_SERVICE_GET_CAR;
	private static final int MAIN_SERVICE_MIN_VERSION = 0;
	private static final int MAIN_SERVICE_MAX_VERSION = Integer.MAX_VALUE;
	
	private List<ServiceQueryFormDTO> dependencies;
	private boolean initialized = false;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public boolean isServable(final ChoreographerExecutorServiceInfoRequestDTO request) {
		return request.getServiceDefinition() == MAIN_SERVICE_DEFINITION
				&& request.getMinVersion() >= MAIN_SERVICE_MIN_VERSION
				&& request.getMaxVersion() <= MAIN_SERVICE_MAX_VERSION;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ChoreographerExecutorServiceInfoResponseDTO createChoreographerExecutorServiceInfoResponseDTO() {
		if (!initialized) {
			initialize();
		}
		
		return new ChoreographerExecutorServiceInfoResponseDTO(MAIN_SERVICE_DEFINITION, MAIN_SERVICE_MIN_VERSION, MAIN_SERVICE_MAX_VERSION, dependencies);
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
		form.setServiceDefinitionRequirement("create-car");
		return form;
	}
}
