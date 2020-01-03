package org.ogema.model.eval;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;
	
/** Single result with description of an evaluation
*/
public interface SingleEvalResult extends Data {
	/**Result value may have a schedule historicalData where the actual result
	 * data is written*/
	FloatResource value();
	/**This can be set by an application using the result, should be supported by
	 * all Evaluation providers
	 */
	BooleanResource writeToSchedule();
	/**Description of the result*/
	StringResource description();
}