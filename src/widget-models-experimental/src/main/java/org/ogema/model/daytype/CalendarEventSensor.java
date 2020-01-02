package org.ogema.model.daytype;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.time.Calendar;
import org.ogema.model.time.CalendarEntry;

/**Event like business travel, guest visits, party, ... that are determined from a calendar application*/
public interface CalendarEventSensor extends Sensor {
	/**Integer value and name*/
	DayType dayType();
	
	/**If true the day type shall be applied to holidays as to the usual setting*/
	BooleanResource isEventActive();
	
	/**List of events in the future (and possible also in the past) including current event if existing
	 * that are determined by calendar application (for planning / evaluation)
	 */
	ResourceList<CalendarEntry> eventList();
	
	Calendar source();
}
