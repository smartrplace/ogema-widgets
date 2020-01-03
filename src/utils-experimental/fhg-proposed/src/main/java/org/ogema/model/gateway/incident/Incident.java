package org.ogema.model.gateway.incident;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.eval.SingleEvalResult;
import org.ogema.model.prototypes.Data;
	
/** See {@link IncidentProvider}.
*/
public interface Incident extends Data {
	/**An id is typically given to an incident type, a repeating incident or to incidents that shall be tracked over more than
	 * one system for identification. The id should also be part of a log message on the generating system that
	 * provides more detailed information in case this is relevant.*/
	StringResource id();
	
	/**Result data for the Incident. If only a single incident is used or all incidents
	 * share the result data, the resource should only be present in the IncidentProvider
	 */
	ResourceList<SingleEvalResult> resultData();
	
	/**Human-readable message explaining the event*/
	StringResource message();
	/** Start time shall be used to represent the event time when no duration shall be specified. For a
	 * repeating incident startTime and endTime give the time span in which the repetition is found. This
	 * usually is reset by clearance.*/
	TimeResource startTime();
	/**End time is optional to be used for events with a duration*/
	TimeResource endTime();
	/**Approximate average duration of single events of a repeating event may be provided here*/
	TimeResource typicalDuration();
	
	/**In this case we just use the result values of StatisticalAggregation (hourValue, dayValue etc.). We count up
	 * the number of events for the current interval and keep the values for the previous interval of each type
	 * evaluated. */
	StatisticalAggregation eventsOnCurrentInterval();
	StatisticalAggregation eventsOnPreviousInterval();
	
	IncidentClearanceStatus clearanceStatus();
	
	/**A priority greater 100 is considered "high" priority, a priority below 10 is "low". To generate unique priority rankings automatically
	 * priority levels may be given on a continuous scale as float, not just as integer.
	 * TODO: define correlation with Log Level?
	 * */
	FloatResource priority();
}