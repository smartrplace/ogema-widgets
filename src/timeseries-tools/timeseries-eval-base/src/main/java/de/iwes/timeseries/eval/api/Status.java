package de.iwes.timeseries.eval.api;

public interface Status {

	public enum EvaluationStatus {
	
		RUNNING,
		FINISHED,
		CANCELLED,
		FAILED,
		RESTART_REQUESTED,
		SKIP_EVALLEVEL
	}
	
	EvaluationStatus getStatus();
	
	/**
	 * Null, unless status is FAILED, in which case it can be non-null 
	 * @return
	 */
	Exception getCause();
	
}
