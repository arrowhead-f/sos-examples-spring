package eu.arrowhead.application.skeleton.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.config.ApplicationInitListener;
import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;
import eu.arrowhead.application.skeleton.executor.execution.ExecutionManager;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;

@Component
public class ExecutorApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ExecutorDriver executorDriver;
	
	@Autowired
	private ExecutionManager executionManager;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SYSTEM_NAME)
	private String mySystemName;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_ADDRESS_WD)
	private String mySystemAddress;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_PORT_WD)
	private int mySystemPort;
	
	private final Logger logger = LogManager.getLogger(ExecutorApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.CHOREOGRAPHER);
		
		//Initialize Arrowhead Context
		arrowheadService.updateCoreServiceURIs(CoreSystem.CHOREOGRAPHER);
		
		//Registering executor into Choreographer
		register();
		
		//Start Executor Manager
		executionManager.start();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		
		//Stop Executor Manager
		executionManager.interrupt();
		
		//Remove executor from Choreographer
		unregister();
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void register() {
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);
		
		final ChoreographerExecutorRequestDTO executorRequestDTO = new ChoreographerExecutorRequestDTO();
		executorRequestDTO.setSystem(systemRequest);
		executorRequestDTO.setServiceDefinitionName(ExecutorConstants.MAIN_SERVICE_GET_CAR);
		executorRequestDTO.setMinVersion(ExecutorConstants.MAIN_SERVICE_GET_CAR_MIN_VERSION);
		executorRequestDTO.setMaxVersion(ExecutorConstants.MAIN_SERVICE_GET_CAR_MAX_VERSION);
		executorRequestDTO.setBaseUri(ExecutorConstants.BASE_URI);
		
		executorDriver.registerExecutor(executorRequestDTO);
		logger.info("Executor registered successfully");
	}
	
	//-------------------------------------------------------------------------------------------------
	private void unregister() {
		executorDriver.unregisterExecutor(mySystemName);
		logger.info("Executor unregistered successfully");
	}
}
