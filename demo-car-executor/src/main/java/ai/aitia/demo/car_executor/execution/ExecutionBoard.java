package ai.aitia.demo.car_executor.execution;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

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
	public Job newJob(final ChoreographerExecuteStepRequestDTO request) {
		synchronized (lock) {
			final Job job = new Job(request, ExecutionSignal.DO);
			board.put(getUinqueIdentifier(request), job);
			queue.add(job);
			return job;			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public Optional<Job> peekJob(final long sessionId, final long sessionStepId) {
		final Job job = board.get(getUinqueIdentifier(sessionId, sessionStepId));
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
			final Job job = board.get(getUinqueIdentifier(sessionId, sessionStepId));
			if (job != null) {
				return job.getExecutionSignal();
			}
			return ExecutionSignal.UNKNOWN;			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void abortJob(final long sessionId, final long sessionStepId) {
		synchronized (lock) {
			final Optional<Job> optional = getJob(sessionId, sessionStepId);
			if (optional.isPresent()) {
				optional.get().setExecutionSignal(ExecutionSignal.ABORT);
			}			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void removeJob(final long sessionId, final long sessionStepId) {
		synchronized (lock) {
			final Job job = board.remove(getUinqueIdentifier(sessionId, sessionStepId));
			queue.remove(job);			
		}
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private String getUinqueIdentifier(final ChoreographerExecuteStepRequestDTO request) {
		return getUinqueIdentifier(request.getSessionId(), request.getSessionStepId());
	}
	
	//-------------------------------------------------------------------------------------------------
	private String getUinqueIdentifier(final long sessionId, final long sessionStepId) {
		return sessionId + "-" + sessionStepId;
	}
}
