package org.ogema.model.extended.alarming;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface IssueActionReport extends Data {
	/** Reference to issue or generic identification resource for non-issue actions*/
	Resource issue();
	
	StringResource type();
	
	StringResource comment();
	
	StringResource link();
	
	TimeResource startTime();
	
	/** Only available if action is finished */
	TimeResource endTime();
	StringResource result();
	IntegerResource lastStepFinished();
	
	/** 0: Unknown<br>
	 *  1: Self-started<br>
	 *  2: Auto (by evaluation via framework)<br>
	 *  3: Manual<br>
	 */
	IntegerResource triggeredBy();
	
	IntegerResource countStarts();
	
	/** Count step and action repetitions. Is only reset when action is restarted after it is
	 * finished completey
	 */
	IntegerResource countStepRepetitions();
}
