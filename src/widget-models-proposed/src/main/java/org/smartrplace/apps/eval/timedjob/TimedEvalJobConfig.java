package org.smartrplace.apps.eval.timedjob;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;

public interface TimedEvalJobConfig extends TimedJobConfig {
	/** Defines the maximum age of the current result of any evaluation that is required in the input path of this evaluation
	 * to avoid recalculation. Any older result in the pre-path trigger a recalculation of the entire path up to this evaluation.
	 * Usually this should be shorter than the automated recalculation interval of this evaluation defined in {@link #alignedInterval()}
	 * and {@link #interval()}.<br>
	 * Note that special settings may be defined in the future to process manual input data.
	 */
	TimeResource maxIntervalForDependentEvaluations();
	
	/** See TimeseriesSimpleProcUtil#updateMode */
	IntegerResource recalculationMode();
}
