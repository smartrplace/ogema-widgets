package org.ogema.model.gateway.incident;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.alignedinterval.TimeIntervalLength;
import org.ogema.model.eval.SingleEvalResult;
import org.ogema.model.prototypes.Data;
	
/** In this context an Incident is an event that is unexpected and/or (typically) unintended. An
 * incident typically requires further processing and clearance either by an automated system or
 * by a support/administration person. If the incident is a bug or otherwise requires development
 * effort it should be converted into a Task/Bug of a task tracking system. For planned
 * events such as sensor events or regular events detected by an algorithm you should
 * use a different modeling approach (e.g. see sensors/actions as examples).<br>
 * <br>Note for the following: Currently we do not support types, but just repeating events, which also
 * should have an id. We have to see whether separate types are really required.
 * <br>
 * A resource of type {@link Incident} may represent an actual event instance or an instance type. Actual
 * events need not to implement a type. Every instance without a time stamp/interval may
 * act as an instance type. Instance types usually describe known error or other unintended system
 * behavior. A special situation is a repeating incident. A repeating incident may not be used as type
 * but counts the number of events for certain intervals and the last occurence of the incident.
 * Typically all event providers should support this to avoid the generation of multiple incident
 * instances when an event occurs repeatedly.<br>
 * Typically an incident is cleared either by time or by a clearance process. Incidents with a priority
 * of high or above are not cleared by time. The clearance process typically is done with manual
 * interaction, but could also be done by some software automatically, which is in the
 * responsibility of all software interacting with incidents.<br>
 * Typically a common IncidentManagement resource can be used by several incident types that
 * use the same parameters defined below.
*/
public interface IncidentProvider extends Data {
	StringResource id();
	/** Different incidents shall be created when events are not just counted (which can be 
	 * presented by counted as RepeatingIncident in {@link #eventsOnCurrentInterval()} and
	 * {@link #eventsOnPreviousInterval()}, but have separate messages and/or clearance stati.
	 * If the incident provider just counts only a single Incident shall be in the list.
	 */
	ResourceList<Incident> incidents();
	
	/**Standard priority to be given to incidents*/
	FloatResource standardPriority();

	IncidentAutoClearanceConfiguration standardAutoClearance();
	/**For providers that cannot be controlled by the user it is recommended to delete the entry with the
	 * last incident removed as the provider is only created when the first incident appears
	 */
	BooleanResource deleteProviderWithLastEvent();
	/**Repeating events are usually not deleted, but just set to zero for relevant intervals when
	 * not occuring anymore. For this reason there may be a longer standard period to keep them in
	 * the list
	 */
	TimeResource deleteRepeatingAfterClearanceStandard();
	
	/**Description, configuration and actual result data. The actual results may be provided
	 * by the Incidents*/
	ResourceList<SingleEvalResult> resultData();
	
	// see OnlineEvalProvider-MultiDemand - put this into resource?
	//MultiDemand inputConfiguration();
	/**Get all other data there like input data. If this id is active the operation shall be controlled
	 * via the respective OnlineEvalProvider, otherwise via the resource stateControl*/
	StringResource onlineEvalProviderId();
	/**If false the provider shall not add any more events*/
	BooleanResource stateControl();
	
	/**Format to convert values into a human readable string. The format shall look like
	 * 'Time:%T(dd.MM HH:mm:ss){startTime} Savings:%.1f{savings} per cent'
	 * This is only relevant for incidents providing evaluation data results
	 */
	StringResource format();
	
	/** Standard interval for StatisticalAggregation if relevant*/
	ResourceList<TimeIntervalLength> standardIntervals();
	
	/**Messaging configurations*/
	ResourceList<IncidentNotificationConfiguration> notificationConfigs();
}