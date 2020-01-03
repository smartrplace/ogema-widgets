package org.ogema.model.gateway.incident;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.alignedinterval.TimeIntervalLength;
import org.ogema.model.prototypes.Data;
	
/** @deprecated use ResourceList<IncidentProvider> instead
*/
@Deprecated
public interface IncidentManagement extends Data {
	ResourceList<Incident> incidents();
	//ResourceList<Incident> types();
	TimeResource clearAfterOccurenceStandard();
	TimeResource deleteAfterClearanceStandard();
	/**Repeating events are usually not deleted, but just set to zero for relevant intervals when
	 * not occuring anymore. For this reason there may be a longer standard period to keep them in
	 * the list
	 */
	TimeResource deleteRepeatingAfterClearanceStandard();
	ResourceList<TimeIntervalLength> standardIntervals();
}