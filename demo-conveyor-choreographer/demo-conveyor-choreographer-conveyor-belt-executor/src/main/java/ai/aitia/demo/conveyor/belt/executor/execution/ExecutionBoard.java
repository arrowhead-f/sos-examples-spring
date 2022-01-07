package ai.aitia.demo.conveyor.belt.executor.execution;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.dto.shared.ChoreographerExecuteStepRequestDTO;

@Component
public class ExecutionBoard {

	//=================================================================================================
	// members
	
	private final Map<String,Job> board = new ConcurrentHashMap<>();
	private final BlockingQueue<Job> queue = new LinkedBlockingQueue<>();
	
	private final Object lock = new Object();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Job newJob(final ChoreographerExecuteStepRequestDTO jobRequest) {
		Assert.notNull(jobRequest, "jobRequest is null");
		
		synchronized (lock) {
			final Job job = new Job(jobRequest, ExecutionSignal.DO);
			board.put(getUniqueIdentifier(jobRequest), job);
			queue.add(job);
			return job;			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public Optional<Job> peekJob(final long sessionId, final long sessionStepId) {
		final Job job = board.get(getUniqueIdentifier(sessionId, sessionStepId));
		if (job != null) {
			return Optional.of(job);
		}
		return Optional.empty();
	}
	
	//-------------------------------------------------------------------------------------------------
	public Job nextJob() throws InterruptedException {
		return queue.take();
	}
	
	//-------------------------------------------------------------------------------------------------
	public ExecutionSignal getJobExecutionSignal(final long sessionId, final long sessionStepId) {
		synchronized (lock) {
			final Job job = board.get(getUniqueIdentifier(sessionId, sessionStepId));
			if (job != null) {
				return job.getExecutionSignal();
			}
			return ExecutionSignal.UNKNOWN;			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void abortJob(final long sessionId, final long sessionStepId) {
		synchronized (lock) {
			final Optional<Job> optional = peekJob(sessionId, sessionStepId);
			if (optional.isPresent()) {
				optional.get().setExecutionSignal(ExecutionSignal.ABORT);
			}			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void removeJob(final long sessionId, final long sessionStepId) {
		synchronized (lock) {
			final Job job = board.remove(getUniqueIdentifier(sessionId, sessionStepId));
			if (job != null) {
				queue.remove(job);							
			}
		}
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private String getUniqueIdentifier(final ChoreographerExecuteStepRequestDTO request) {
		return getUniqueIdentifier(request.getSessionId(), request.getSessionStepId());
	}
	
	//-------------------------------------------------------------------------------------------------
	private String getUniqueIdentifier(final long sessionId, final long sessionStepId) {
		return sessionId + "-" + sessionStepId;
	}
}
