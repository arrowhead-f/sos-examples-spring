package ai.aitia.demo.conveyor.belt.executor.execution.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.demo.conveyor.belt.executor.Constant;
import ai.aitia.demo.conveyor.belt.executor.execution.ExecutionSignal;
import ai.aitia.demo.conveyor.belt.executor.execution.Job;
import ai.aitia.demo.conveyor.belt.executor.model.service.TransportWithConveyorService;
import eu.arrowhead.application.skeleton.executor.service.ExecutorDriver;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.shared.ChoreographerExecutedStepStatus;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;

public class TransportWithConveyorServiceExecutionWorker implements Runnable {

	//=================================================================================================
	// members
	
	private final Job job;	
	
	@Autowired
	private ExecutorDriver driver;
	
	@Autowired
	private ArrowheadService ahService;
	
	@Autowired
	private SSLProperties sslProps;
	
	private final Logger logger = LogManager.getLogger(TransportWithConveyorServiceExecutionWorker.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public TransportWithConveyorServiceExecutionWorker(final Job job) {
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
			final OrchestrationResultDTO orchResult = job.getJobRequest().getMainOrchestrationResult();
			
			try {
				ahService.consumeServiceHTTP(Void.class,
											 getMethod(orchResult),
											 orchResult.getProvider().getAddress(), orchResult.getProvider().getPort(),
											 orchResult.getServiceUri(),
											 getInterface(),
											 getToken(orchResult),
											 null,
											 getQueryParams());
				
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.SUCCESS,
		   				   				   "Conveyor belt has been moved", null);
				
			} catch (final Exception ex) {
				logger.error(ex.getMessage());
				logger.debug(ex);
				driver.notifyChoreographer(job.getJobRequest().getSessionId(), job.getJobRequest().getSessionStepId(), ChoreographerExecutedStepStatus.ERROR,
		   				   				   "Error has been occured while consuming service" + TransportWithConveyorService.SERVICE_DEFINITION, ex.getMessage());
			}
		}
	}
	
	//=================================================================================================
	// assistant methods
    
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
    
    //-------------------------------------------------------------------------------------------------
    private String[] getQueryParams() {
    	final String distanceValue = job.getJobRequest().getStaticParameters().get(TransportWithConveyorService.QUERY_PARAM_SIGNED_DISTANCE);
		if (distanceValue  == null || distanceValue.isEmpty()) {
			return new String[0];
		}
    	final String[] params = {TransportWithConveyorService.QUERY_PARAM_SIGNED_DISTANCE, distanceValue};
    	return params;
    }
}
