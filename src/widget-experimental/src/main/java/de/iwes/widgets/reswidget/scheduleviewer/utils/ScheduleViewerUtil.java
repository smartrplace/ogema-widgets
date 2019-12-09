/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.widgets.reswidget.scheduleviewer.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.actors.Actor;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.Sensor;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.timeseries.api.MemoryTimeSeries;
import org.ogema.tools.timeseries.implementations.TreeTimeSeries;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilterExtended;
import de.iwes.widgets.template.DisplayTemplate;

public class ScheduleViewerUtil {

	private static ScheduleViewerUtil instance;
	public static final String FIRST_POSSIBLE_DATE = "firstPosibleDateOfSchedule";
	public static final String LAST_POSSIBLE_DATE = "lastPosibleDateOfSchedule";
	public static final String STARTTIME_PRESELECTED = "startTimePreselected";
	public static final String ENDTIME_PRESELECTED = "endTimePreselected";

	private ScheduleViewerUtil() {

	}

	public static ScheduleViewerUtil getInstance() {
		if (ScheduleViewerUtil.instance == null) {
			ScheduleViewerUtil.instance = new ScheduleViewerUtil();
		}
		return ScheduleViewerUtil.instance;
	}

	public Collection<TimeSeriesFilter> getTimeSeriesFilter(ScheduleFilterEnum... scheduleFilterEnum) {

		Collection<TimeSeriesFilter> program = new ArrayList<>();

		for (ScheduleFilterEnum filtername : scheduleFilterEnum) {
			switch (filtername) {
			case ALL_TIME_SERIES:
				program.add(TimeSeriesFilter.ALL_TIME_SERIES);
				break;
			case ALL_FORECASTS:
				program.add(TimeSeriesFilter.ALL_FORECASTS);
				break;
			case SCHEDULES_ONLY:
				program.add(TimeSeriesFilter.SCHEDULES_ONLY);
				break;
			case ALL_POWER:
				program.add(TimeSeriesFilter.ALL_POWER);
				break;
			case OTHER_SENSORS:
				program.add(TimeSeriesFilter.OTHER_SENSORS);
				break;
			case POWER_MEASUREMENTS:
				program.add(TimeSeriesFilter.POWER_MEASUREMENTS);
				break;
			case ALL_TEMPERATURES:
				program.add(TimeSeriesFilter.ALL_TEMPERATURES);
				break;
			case THERMOSTAT_SETPOINTS:
				program.add(TimeSeriesFilter.THERMOSTAT_SETPOINTS);
				break;
			case TEMPERATURE_MANAGEMENT_SETPOINTS:
				program.add(TimeSeriesFilter.TEMPERATURE_MANAGEMENT_SETPOINTS);
				break;
			case ALL_HUMIDITIES:
				program.add(TimeSeriesFilter.ALL_HUMIDITIES);
				break;
			case TEMPERATURE_MEASUREMENTS:
				program.add(TimeSeriesFilter.TEMPERATURE_MEASUREMENTS);
				break;
			case HUMIDITY_MEASUREMENTS:
				program.add(TimeSeriesFilter.HUMIDITY_MEASUREMENTS);
				break;
			case VALVE_POSITIONS:
				program.add(TimeSeriesFilter.VALVE_POSITIONS);
				break;
			default:
				break;
			}

		}
		return program;
	}

	/**
	 * Returns the start and end times by having the schedules values
	 * @param schedules
	 * @return
	 */
	public static long[] getStartEndTime(List<ReadOnlyTimeSeries> schedules) {
		long[] startEnd = {-1, -1};
		if (schedules == null || schedules.isEmpty()) {
			return startEnd;
		} else {
			long min = Long.MAX_VALUE;
			long max = 0;

			for (ReadOnlyTimeSeries schedule : schedules) {
				if (schedule == null || schedule.isEmpty()) {
					continue;
				}
				long firstValue = schedule.getNextValue(0).getTimestamp();
				long lastValue = schedule.getPreviousValue(Long.MAX_VALUE).getTimestamp();
				min = Math.min(min, firstValue);
				max = Math.max(max, lastValue);
			}

			if (min == Long.MAX_VALUE && max == 0) {
				return startEnd;
			}
			startEnd[0] = min;
			startEnd[1] = max;
		}
		return startEnd;
	}

	/**
	 * parsed the provider's Time Series Filter to TimeSeries Filter Extended with longname, shortname, and location
	 * @param list
	 * @param ra
	 * @return
	 */
	public List<Collection<TimeSeriesFilterExtended>> parse(List<Collection<TimeSeriesFilter>> list,
			ResourceAccess ra) {
		List<Collection<TimeSeriesFilterExtended>> result = new ArrayList<>();

		for (Collection<TimeSeriesFilter> collection : list) {
			List<TimeSeriesFilterExtended> extended = parse(collection, ra);
			result.add(extended);
		}
		return result;
	}

	/**
	 * reparsed the provider's Time SeriesFilterExtended to TimeSeriesFilter
	 * @param items
	 * @return
	 */
	public static List<TimeSeriesFilter> reparse(List<TimeSeriesFilterExtended> items) {
		List<TimeSeriesFilter> result = new ArrayList<>();
		for (TimeSeriesFilterExtended extended : items) {
			result.add((TimeSeriesFilter) extended);
		}
		return result;
	}

	/**
	 * parsed the provider's Time Series Filter to TimeSeries Filter Extended with longname, shortname, and location
	 * @param collection
	 * @param ra
	 * @return
	 */
	public List<TimeSeriesFilterExtended> parse(Collection<TimeSeriesFilter> collection, ResourceAccess ra) {

		List<TimeSeriesFilterExtended> result = new ArrayList<>();
		for (TimeSeriesFilter filter : collection) {
			if(filter instanceof TimeSeriesFilterExtended) {
				result.add((TimeSeriesFilterExtended) filter);
				continue;
			}
			TimeSeriesFilterExtended filterExtended = new TimeSeriesFilterExtended() {

				@Override
				public String label(OgemaLocale locale) {
					return filter.label(locale);
				}

				@Override
				public String id() {
					return filter.id();
				}

				@Override
				public boolean accept(ReadOnlyTimeSeries schedule) {
					return filter.accept(schedule);
				}

				@Override
				public String shortName(ReadOnlyTimeSeries schedule) {
					return getScheduleShortName(schedule, ra);
				}

				@Override
				public String longName(ReadOnlyTimeSeries schedule) {
					return getScheduleLongName(schedule, filter.label(null), ra);
				}

				@Override
				public Class<?> type(ReadOnlyTimeSeries schedule) {
					//we provide no definition here
					return null;
				}
			};
			result.add(filterExtended);
		}

		return result;
	}
	
	/** Get short name for any schedule
	 * 
	 * @param schedule
	 * @param ra may be null; if provided the path of the resource of a RecordedData time series can be used
	 * @return
	 */
	public static String getScheduleShortName(ReadOnlyTimeSeries schedule, ResourceAccess ra) {
		
		if (schedule instanceof RecordedData) {
			RecordedData s = (RecordedData) schedule;
			String path = s.getPath();
			if(ra == null) return path;
			Resource sourceResource;
			try {
				sourceResource = ra.getResource(path);
			} catch (SecurityException expected) {
				return path;
			}
			if(sourceResource == null) return path;
			return ResourceUtils.getHumanReadableShortName(sourceResource);
		}
		
		if (schedule instanceof Schedule) {
			Schedule s = (Schedule) schedule;
			return s.getName();
		}

		if (schedule instanceof TreeTimeSeries) {
			return "unknown TreeTimeSeries";
		}

		if (schedule instanceof MemoryTimeSeries) {
			return "unknown MemoryTimeSeries";
		}

		return "unknown shortname";
	}

	/** Get short name for any schedule
	 * 
	 * @param schedule
	 * @param deviceTypeName in case nothing else works provide a device name for identification here
	 * @param ra may be null; if provided the path of the resource of a RecordedData time series can be used
	 * @return
	 */
	public static String getScheduleLongName(ReadOnlyTimeSeries schedule,
			String deviceTypeName, ResourceAccess ra) {

		if (schedule instanceof TreeTimeSeries) {
			return "unknown TreeTimeSeries";
		}

		if (schedule instanceof MemoryTimeSeries) {
			return "unknown MemoryTimeSeries";
		}

		Resource sourceResource = null;
		if (schedule instanceof Schedule) {
			Schedule s = (Schedule) schedule;
			sourceResource = s.getLocationResource();
		}
		if (schedule instanceof RecordedData) {
			RecordedData s = (RecordedData) schedule;
			String path = s.getPath();
			if(ra == null) return deviceTypeName+"(D)";
			try {
				sourceResource = ra.getResource(path);
			} catch (SecurityException expected) {
				return path + " (by SecurityException)";
			}
		}

		//String deviceTypeName = filter.label(null);
		String sensorActorType = null;

		String result = "";
		// "{<type>} in {<room>} with {Sensor|Actor} {<name>}"
		// "Temperatures in Arbeitszimmer with Sensor/Actor Homematic_4711"

		if (deviceTypeName != null) {
			result = deviceTypeName + " ";
		}

		final Room room = ResourceUtils.getDeviceRoom(sourceResource);
		if (room != null) {
			result += "in " + room.name().getValue();
		}
		
		if(sourceResource instanceof PowerResource) {
			PowerResource pow = (PowerResource) sourceResource;
			String path = pow.getPath();
			if(path.contains("phase1")) {
				result += "in " + getLabelPowerSens(pow, "phase1");
			}else if(path.contains("phase2")) {
				result += "in " + getLabelPowerSens(pow, "phase2");
			}else if(path.contains("phase3")) {
				result += "in " + getLabelPowerSens(pow, "phase3");
			}else {
				result += "in " + getLabelPowerSens(pow, "Overall");
			}	
		}
		
		if(sourceResource.getPath().contains("aggregatedValues")) {						
			return getLabelAggregatedLinkingResource(sourceResource);
		}
		
		
		sensorActorType = getSensorActorType(sourceResource);
		if (sensorActorType != null) {
			if (result.isEmpty()) {
				result += sensorActorType; 
			} else {
				result += " with " + sensorActorType;
			}
		}

		if (room == null && sensorActorType == null) {
			if (!result.isEmpty()) {
				result += " in ";
			}
			result += ResourceUtils.getHumanReadableName(sourceResource) + ", ";
		}

		if (result.isEmpty()) {
			return null;
		}
		return result;
	}
	
	/**
	 * Helper method for formatting the name
	 * @param resource
	 * @return <topLevelName>-<subName>( *)
	 */
	private static String getLabelPowerSens(PowerResource resource, String subName) {
		String path = resource.getPath();
		String topLevelName = path.substring(0, path.indexOf("/"));
		String label = topLevelName + "_" + subName;

		return label;
	}
	
	/**
	 * Helper method for formatting the name
	 * @param r
	 * @return
	 */
	private static String getLabelAggregatedLinkingResource(final Resource r) {
		try {
			Resource intervall = r.getParent();
			Resource location = r.getParent().getParent();
			String label = "Aggregated "+location.getName() + " (" + intervall.getName() + "s)";
			return label;
		}catch (Exception e) {
			return r.getPath();
		}
	}


	/**
	 * Rrturns Sensor if the Device is a Sensor, or Actor, else null 
	 * @param device
	 * @return
	 */
	private static String getSensorActorType(Resource device) {

		while (device != null) {
			if (device instanceof Sensor) { // TemperatureSensor...
				Sensor s = (Sensor) device;
				return " Sensor: "+ s.getResourceType().getSimpleName();
			}
			if (device instanceof Actor) { // OnOffSwitch...
				Actor a = (Actor) device;
				return " Actor: "+ a.getResourceType().getSimpleName();
			}
			try {
				device = device.getParent();
			} catch (SecurityException expected) {
				return null;
			}
			
		}
		return null;
	}

	
	public static String getPageParameter(OgemaHttpRequest req, WidgetPage<?> page, String paramName) {
		try {
			return page.getPageParameters(req).get(paramName)[0];
		} catch (Exception e) {
			// Do nothing
		}
		return null;
	}
	
	
	/**
	 * Returns the preselected start and end time coded in the URL
	 * @param req
	 * @param page
	 * @return
	 */
	public static Long[] getStartEndTimeFromParameter(OgemaHttpRequest req, WidgetPage<?> page) {
		long now = System.currentTimeMillis();
		Long[] array = new Long[] {now,now};
		String start = getPageParameter(req, page, STARTTIME_PRESELECTED);
		String end = getPageParameter(req, page, ENDTIME_PRESELECTED);

		if(start != null) {
			try {
				array[0] = Long.valueOf(start);
			}catch (Exception e) {
				array[0] = System.currentTimeMillis();
			}
		}
		if(end != null) {
			try {
				array[1] = Long.valueOf(end);
			}catch (Exception e) {
				array[1] = System.currentTimeMillis();
			}
		}
		
		return array;
	}

	public enum ScheduleFilterEnum {
		ALL_TIME_SERIES, ALL_FORECASTS, LOG_DATA_ONLY, SCHEDULES_ONLY, ALL_POWER, OTHER_SENSORS, POWER_MEASUREMENTS, ALL_TEMPERATURES, THERMOSTAT_SETPOINTS, TEMPERATURE_MANAGEMENT_SETPOINTS, ALL_HUMIDITIES, TEMPERATURE_MEASUREMENTS, HUMIDITY_MEASUREMENTS, VALVE_POSITIONS;
	}
	
	public final static Comparator<DropdownOption> defaultLongComparator = new Comparator<DropdownOption>() {

		@Override
		public int compare(DropdownOption d1, DropdownOption d2) {
			long o1 = 0;
			long o2 = 0;
			try {
				o1 = Long.parseLong(d1.id());
			} catch (Exception e) {
			}
			try {
				o2 = Long.parseLong(d2.id());
			} catch (Exception e) {
			}
			return (int) (o1 - o2);
		}
	};

	public final static DisplayTemplate<Long> intervalDisplayTemplate = new DisplayTemplate<Long>() {

		@Override
		public String getId(Long object) {
			return String.valueOf(object);
		}

		@Override
		public String getLabel(Long object, OgemaLocale locale) {
			return object == 0 ? "all"
					: object == 10 * 60 * 1000L ? "last 10 minutes"
							: object == 60 * 60 * 1000L ? "last hour"
									: object == 24 * 60 * 60 * 1000L ? "last day"
											: object == 2 * 24 * 60 * 60 * 1000L ? "last two days"
													: object == 7 * 24 * 60 * 60 * 1000L ? "last week"
															: object == 30 * 24 * 60 * 60 * 1000L ? "last month"
																	: object == 365 * 24 * 60 * 60 * 1000L ? "last year"
																			: String.valueOf(object) + "ms"; // should
																												// not
																												// occur
		}
	};

}
