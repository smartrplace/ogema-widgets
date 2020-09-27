package org.sp.bacnet.models;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;
import org.ogema.model.ranges.TimeRange;

public interface BacNetCalendarEntry extends Data {
	/** Usually only a single element of the following should be present*/
	TimeResource date();
	TimeRange range();
	IntegerResource dayOfWeek();
}
