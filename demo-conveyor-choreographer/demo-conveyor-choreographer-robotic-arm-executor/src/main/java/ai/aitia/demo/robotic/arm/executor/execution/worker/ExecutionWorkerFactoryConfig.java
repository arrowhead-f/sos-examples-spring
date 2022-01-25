package ai.aitia.demo.robotic.arm.executor.execution.worker;

import java.util.function.Function;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import ai.aitia.demo.robotic.arm.executor.execution.Job;
import ai.aitia.demo.robotic.arm.executor.model.service.TakeOffService;

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
		final String serviceDefinition = job.getJobRequest().getMainOrchestrationResult().getService().getServiceDefinition().trim().toLowerCase();
		
		switch (serviceDefinition) {
		
		case TakeOffService.SERVICE_DEFINITION:
			return new TakeOffServiceExecutionWorker(job);

		default:
			return new UnkownServiceExecutionWorker(job);
		} 
	}
}
