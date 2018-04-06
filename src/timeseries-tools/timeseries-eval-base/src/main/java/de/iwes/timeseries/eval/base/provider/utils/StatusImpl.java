package de.iwes.timeseries.eval.base.provider.utils;

import de.iwes.timeseries.eval.api.Status;

public class StatusImpl implements Status {
	
	public static final Status DONE = new StatusImpl(EvaluationStatus.FINISHED, null);
	public static final Status RUNNING = new StatusImpl(EvaluationStatus.RUNNING, null);
	public static final Status CANCELLED = new StatusImpl(EvaluationStatus.CANCELLED, null);	
	public static final Status RESTART_REQUESTED = new StatusImpl(EvaluationStatus.RESTART_REQUESTED, null);	
	public static final Status SKIP_EVALLEVEL = new StatusImpl(EvaluationStatus.SKIP_EVALLEVEL, null);	
	private final EvaluationStatus status;
	private final Exception cause;
	
	public StatusImpl(EvaluationStatus status, Exception cause) {
		this.status = status;
		this.cause = cause;
	}

	@Override
	public EvaluationStatus getStatus() {
		return status;
	}

	@Override
	public Exception getCause() {
		return cause;
	}

}
