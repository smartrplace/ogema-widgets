package org.ogema.model.recplay.testing;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** Deviations may occur due to differences in timing (which includes unexpected or missing events)
 * and due to value deviations. For now value deviations are just documented via the description.
 */
public interface RecReplayDeviation extends Data {
	Resource reference();
	StringResource description();
	
	/** If timeExpected does not exist then the occurence was not expected at all*/
	TimeResource timeExpected();
	/** If timeOccured does not exist then an expected event was not detected at all*/
	TimeResource timeOccured();
}
