/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package de.iwes.widgets.reswidget.scheduleviewer.clone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilterExtended;

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


	public static Map<String, Long> getStartEndTime(List<ReadOnlyTimeSeries> schedules) {

		Map<String, Long> map = new HashMap<>();
		if (schedules == null || schedules.isEmpty()) {
			return Collections.emptyMap();
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
				return Collections.emptyMap();
			}
			map.put(FIRST_POSSIBLE_DATE, min);
			map.put(LAST_POSSIBLE_DATE, max);
		}
		return map;
	}

	public List<Collection<TimeSeriesFilterExtended>> parse(List<Collection<TimeSeriesFilter>> list,
			ResourceAccess ra) {
		List<Collection<TimeSeriesFilterExtended>> result = new ArrayList<>();

		for (Collection<TimeSeriesFilter> collection : list) {
			List<TimeSeriesFilterExtended> extended = parse(collection, ra);
			result.add(extended);
		}
		return result;
	}

	public static List<TimeSeriesFilter> reparse(List<TimeSeriesFilterExtended> items) {
		List<TimeSeriesFilter> result = new ArrayList<>();
		for (TimeSeriesFilterExtended extended : items) {
			result.add((TimeSeriesFilter) extended);
		}
		return result;
	}

	public List<TimeSeriesFilterExtended> parse(Collection<TimeSeriesFilter> collection, ResourceAccess ra) {

		List<TimeSeriesFilterExtended> result = new ArrayList<>();
		for (TimeSeriesFilter filter : collection) {

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
				public List<ReadOnlyTimeSeries> timeSeriesAccepted() {
					return null;
				}

				@Override
				public String shortName(ReadOnlyTimeSeries schedule) {
					
					if (schedule instanceof RecordedData) {
						RecordedData s = (RecordedData) schedule;
						String path = s.getPath();
						Resource sourceResource;
						try {
							sourceResource = ra.getResource(path);
						} catch (SecurityException expected) {
							return path;
						}
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

				@Override
				public String longName(ReadOnlyTimeSeries schedule) {

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
						try {
							sourceResource = ra.getResource(path);
						} catch (SecurityException expected) {
							return path;
						}
					}

					String deviceTypeName = null;
					String sensorActorType = null;
					deviceTypeName = filter.label(null);

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
							result += "in " + getPowerSensLabel(pow, "phase1");
						}else if(path.contains("phase2")) {
							result += "in " + getPowerSensLabel(pow, "phase2");
						}else if(path.contains("phase3")) {
							result += "in " + getPowerSensLabel(pow, "phase3");
						}else {
							result += "in " + getPowerSensLabel(pow, "Overall");
						}
	
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
			};
			result.add(filterExtended);
		}

		return result;
	}
	
	/**
	 * 
	 * @param resource
	 * @return <topLevelName>-<subName>( *)
	 */
	private String getPowerSensLabel(PowerResource resource, String subName) {
		String path = resource.getPath();
		String topLevelName = path.substring(0, path.indexOf("/"));
		String label = topLevelName + "_" + subName;

		return label;
	}



	private String getSensorActorType(Resource device) {

		while (device != null) {
			if (device instanceof Sensor) { // TemperatureSensor...
				Sensor s = (Sensor) device;
				return " Sensor: "+ s.getResourceType().getSimpleName();
			}
			if (device instanceof Actor) { // OnOffSwitch...
				Actor a = (Actor) device;
				return " Actor: "+ a.getResourceType().getSimpleName();
			}
			device = device.getParent();
		}
		return null;
	}

	public static String getPageParameter(OgemaHttpRequest req, WidgetPage<?> page, String paramName) {
		String param = null;
		try {
			param = page.getPageParameters(req).get(paramName)[0];
		} catch (Exception e) {
		}
		return param;
	}
	
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

}
