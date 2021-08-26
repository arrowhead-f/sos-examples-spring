package eu.arrowhead.application.skeleton.executor.execution.worker;

import java.util.function.Function;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import eu.arrowhead.application.skeleton.executor.ExecutorConstants;
import eu.arrowhead.application.skeleton.executor.execution.Job;

@Configuration
public class ExecutionWorkerFactoryConfig {

	//-------------------------------------------------------------------------------------------------
	@Bean
	public Function<Job,Runnable> executionWorkerFactory() {
		return job -> createExecutionWorker(job);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public Runnable createExecutionWorker(final Job job) {
		final String serviceDefinition = job.getJobRequest().getMainOrchestrationResult().getService().getServiceDefinition();
		
		switch (serviceDefinition) {
		case ExecutorConstants.MAIN_SERVICE_GET_CAR:
			return new GetCarServiceExecutionWorker(job);

		default:
			return new UnkownServiceExecutionWorker();
		} 
	}
}
