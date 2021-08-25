package eu.arrowhead.application.skeleton.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.config.ApplicationInitListener;
import ai.aitia.demo.car_executor.execution.ExecutionManager;
import eu.arrowhead.common.core.CoreSystem;

@Component
public class ExecutorApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ExecutionManager executionManager;
	
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
		
		//Start Executor Manager
		executionManager.start();
		
		//TODO: implement here any custom behavior on application start up
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		
		//Stop Executor Manager
		executionManager.interrupt();
		
		//TODO: implement here any custom behavior on application shout down
	}
}
