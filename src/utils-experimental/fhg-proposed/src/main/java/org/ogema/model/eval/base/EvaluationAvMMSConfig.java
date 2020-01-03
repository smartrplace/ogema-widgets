package org.ogema.model.eval.base;

import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.eval.EvaluationConfig;
	
/** Evaluation of a (typically single) input into average, minimum, maximum, standard deviation. Not
 * all of these outputs may be actually provided. 
*/
public interface EvaluationAvMMSConfig extends EvaluationConfig {
	/** Average
	 * TODO: This is not ideal. What to do with destinationStat in such multiple-output evaluations?
	 */
	@Override
	StatisticalAggregation destinationStat();
	
	/** Root mean square (if this exists usually average is not provided)*/
	StatisticalAggregation rms();
	/** Standard deviation*/
	StatisticalAggregation std();
	/** Minimum*/
	StatisticalAggregation min();
	/** Maximum*/
	StatisticalAggregation max();
}