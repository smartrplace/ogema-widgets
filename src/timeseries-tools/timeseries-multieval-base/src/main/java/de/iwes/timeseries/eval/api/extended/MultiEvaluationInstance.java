package de.iwes.timeseries.eval.api.extended;

import java.util.List;
import java.util.concurrent.Future;

import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;

/**
 * A time series evaluation instance. 
 * 
 * Note to implementers: implementations must inherit from the
 * base class {@link EvaluationBaseImpl}. 
 */
public interface MultiEvaluationInstance<R, T extends MultiResult<R>> extends Future<Status> {
	
	/**
	 * An id, specified by the evaluation provider
	 * @return
	 */
	String id();
	
	/**
	 * Contains the results of the evaulation
	 * @throws IllegalStateException 
	 * 		if called before the evaluation is done, or the evaluation failed
	 * @return
	 */
	AbstractSuperMultiResult<R, T> getResult() throws IllegalStateException;
	
	List<MultiEvaluationInputGeneric<R>> getInputData();
	
	boolean isOnlineEvaluation();
	
	@SuppressWarnings("rawtypes")
	<X extends MultiResult> Class<X> getResultType();
	<S extends AbstractSuperMultiResult<R, T>> Class<S> getSuperResultType();
	
	/**
	 * The listener will be called when the evaluation is done, or 
	 * immediately, if it is already finished.
	 * @param listener
	 */
	void addListener(MultiEvaluationListener<R, T> listener);
	
	/*
	 * Methods for internal use
	 */
	
	/**
	 * Method for internal use
	 */
	Status finish();

	/**
	 * A listener that is informed about the evaluation being done.
	 */
	public static interface MultiEvaluationListener<R, T extends MultiResult<R>> {
		
		void evaluationDone(MultiEvaluationInstance<R, ?> evaluation, Status status);
		
	}

	public Status execute();
	
}
