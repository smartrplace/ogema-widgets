package org.sp.bacnet.models;

import org.ogema.core.model.ResourceList;
import org.ogema.model.prototypes.Data;

public interface BacNetCalendar extends Data {
	ResourceList<BacNetCalendarEntry> entries();
}
