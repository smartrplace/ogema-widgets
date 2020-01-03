package org.ogema.model.eval;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.prototypes.Data;
	
/** Event type provided by evaluation
 * @deprecated no implementation using this available, use OnlineEvalProvider instead
*/
public interface EvaluationEvent extends Data {
	/**If the event is indicated by setting a BooleanResource to true use this. Otherwise define the appropriate
	 * date for the event*/
	BooleanResource state();
}