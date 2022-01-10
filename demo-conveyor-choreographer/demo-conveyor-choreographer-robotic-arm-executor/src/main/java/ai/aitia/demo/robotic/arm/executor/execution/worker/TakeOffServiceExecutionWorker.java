package ai.aitia.demo.robotic.arm.executor.execution.worker;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.demo.robotic.arm.executor.Constant;
import ai.aitia.demo.robotic.arm.executor.execution.ExecutionSignal;
import ai.aitia.demo.robotic.arm.executor.execution.Job;
import ai.aitia.demo.robotic.arm.executor.model.dto.DuckSeenResponseDTO;
import ai.aitia.demo.robotic.arm.executor.model.service.TakeOffService;
import ai.aitia.demo.robotic.arm.executor.model.service.dependency.DuckSeenService;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;

public class TakeOffServiceExecutionWorker implements Runnable {
	
	//=================================================================================================
	// members
	
	private final Job job;
	
	@Autowired
	private ExecutorDriver driver;
	
	@Autowired
	private ArrowheadService ahService;
	
	@Autowired
	private SSLProperties sslProps;
	
	private final Logger logger = LogManager.getLogger(TakeOffServiceExecutionWorker.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	public TakeOffServiceExecutionWorker(final Job job) {
		this.job = job;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		if (job.getExecutionSignal() == ExecutionSignal.ABORT) {
			logger.info(job.getJobRequest().getMainOrchestrationResult().getService().getServiceDefinition() + " is aborted");
			driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.ABORTED,
	   				   				   job.getJobRequest().getMainOrchestrationResult().getService().getServiceDefinition() + " is aborted", null);
		}
		
		if (job.getExecutionSignal() == ExecutionSignal.DO) {
			//Dependency service
			final OrchestrationResultDTO duckSeenService = retrieveDuckSeenService(job.getJobRequest().getPreconditionOrchestrationResults());
			if (duckSeenService == null) {
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.ERROR,
		   				   				   "Have no 'duck-seen' dependency service", "Missing dependency service");
				return;
			}
			final DuckSeenResponseDTO duckSeenResp;
			try {
				duckSeenResp = ahService.consumeServiceHTTP(DuckSeenResponseDTO.class,
															getMethod(duckSeenService),
															duckSeenService.getProvider().getAddress(), duckSeenService.getProvider().getPort(),
															duckSeenService.getServiceUri(),
															getInterface(),
															getToken(duckSeenService),
															null);
				
			} catch (final Exception ex) {
				logger.error(ex.getMessage());
				logger.debug(ex);
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.ERROR,
		   				   				   "Error has been occured while consuming service" + DuckSeenService.SERVICE_DEFINITION, ex.getMessage());
				return;
			}
			if (!duckSeenResp.isSeen()) {
				logger.info("No duck has been recognized");
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.SUCCESS,
										   "No duck has been recognized", null);
				return;
			}
			
			//Main service
			final OrchestrationResultDTO takeOffService = job.getJobRequest().getMainOrchestrationResult();
			try {
				ahService.consumeServiceHTTP(Void.class,
						  getMethod(takeOffService),
						  takeOffService.getProvider().getAddress(), takeOffService.getProvider().getPort(),
						  takeOffService.getServiceUri(),
						  getInterface(),
						  getToken(takeOffService),
						  duckSeenResp.getCoordinate());
				
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.SUCCESS,
		   				   				   "Duck has been recognized and taken off", null);
			} catch (final Exception ex) {
				logger.error(ex.getMessage());
				logger.debug(ex);
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.ERROR,
		   				   				   "Error has been occured while consuming service" + TakeOffService.SERVICE_DEFINITION, ex.getMessage());
			}
		}		
	}

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private OrchestrationResultDTO retrieveDuckSeenService(final List<OrchestrationResultDTO> dependencyResults) {
		OrchestrationResultDTO dependency = null;
		for (final OrchestrationResultDTO result : dependencyResults) {
			if (result.getService().getServiceDefinition().equalsIgnoreCase(DuckSeenService.SERVICE_DEFINITION)) {
				dependency = result;
				break;
			}
		}
		return dependency;
	}
	
	//-------------------------------------------------------------------------------------------------
    private HttpMethod getMethod(final OrchestrationResultDTO orchResult) {
    	return HttpMethod.resolve(orchResult.getMetadata().get(Constant.HTTP_METHOD));
    }
	
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProps.isSslEnabled() ? Constant.INTERFACE_SECURE : Constant.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private String getToken(final OrchestrationResultDTO orchResult) {
    	return orchResult.getAuthorizationTokens() == null ? null : orchResult.getAuthorizationTokens().get(getInterface());
    }
}
