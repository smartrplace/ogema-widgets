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
package de.iwes.widgets.reswidget.scheduleviewer.api;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.PowerSensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public interface TimeSeriesFilter { //extends LabelledItem {
	
	String id();
	
	/**
	 * A label for display in the user interface
	 * @param locale
	 * @return
	 */
	String label(OgemaLocale locale);
	
	/**
	 * Determines whether the time series is selected or not, when this program (filter) 
	 * is applied.
	 * @param schedule
	 * @return
	 */
	boolean accept(ReadOnlyTimeSeries schedule);
	
	/*
	 ******* Below are examples of filters, which can be used in applications ******
	 */
	
	public static final TimeSeriesFilter ALL_TIME_SERIES = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "All time series";
		}
		
		@Override
		public String id() {
			return "all";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries arg0) {
			return true;
		}
	};
	
	
	public static final TimeSeriesFilter ALL_FORECASTS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "All forecasts";
		}
		
		@Override
		public String id() {
			return "forecasts";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries arg0) {
			if (!(arg0 instanceof Schedule))
				return false;
			return ((Schedule) arg0).getName().toLowerCase().contains("forecast");
		}
	};
	
	public static final TimeSeriesFilter LOG_DATA_ONLY = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Log data";
		}
		
		@Override
		public String id() {
			return "log_data";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries arg0) {
			return arg0 instanceof RecordedData;
		}
	};
	
	public static final TimeSeriesFilter SCHEDULES_ONLY = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Schedules (time series resources)";
		}
		
		@Override
		public String id() {
			return "schedules";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries arg0) {
			return arg0 instanceof Schedule;
		}
	};

	public static final TimeSeriesFilter ALL_POWER = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Power values";
		}
		
		@Override
		public String id() {
			return "power_all";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				return parent instanceof PowerResource;
			}
			if (!(schedule instanceof RecordedData))
				return false;
			final String id = ((RecordedData) schedule).getPath();
			String[] components = id.split("/");
			int length = components.length;
			if (length < 2)
				return false;
			String last = components[length-1];
			String secondLast = components[length-2].toLowerCase();
			switch (last) {
			case "reading":
				return secondLast.contains("power");
			default:
				return false;
			}
		}
	};
	
	/**
	 * For other sensor values than temperature, power and humidity, which have their own specific filters
	 */
	public static final TimeSeriesFilter OTHER_SENSORS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale locale) {
			return "Other sensor values";
		}
		
		@Override
		public String id() {
			return "other_sensors";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				if (parent != null)
					parent = parent.getParent();
				if (!(parent instanceof Sensor))
					return false;
				return (!(parent instanceof TemperatureSensor) && !(parent instanceof HumiditySensor) && !(parent instanceof PowerSensor));
			} else if (schedule instanceof RecordedData) {
				final String id = ((RecordedData) schedule).getPath();
				String[] components = id.split("/");
				int length = components.length;
				if (length < 2)
					return false;
				String last = components[length-1];
				String secondLast = components[length-2].toLowerCase();
				switch (last) {
				case "reading":
					final String lower = secondLast.toLowerCase();
					return lower.contains("sensor") && !lower.contains("temperature") && !lower.contains("power") && !lower.contains("humidity");
				default:
					return false;
				}
			} else
				return false;
		}
	};
	
	public static final TimeSeriesFilter POWER_MEASUREMENTS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Power measurements";
		}
		
		@Override
		public String id() {
			return "power_measurements";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (!(schedule instanceof RecordedData))
				return false;
			final String id = ((RecordedData) schedule).getPath();
			String[] components = id.split("/");
			int length = components.length;
			if (length < 2)
				return false;
			String last = components[length-1];
			String secondLast = components[length-2].toLowerCase();
			switch (last) {
			case "reading":
				return secondLast.contains("power");
			default:
				return false;
			}
		}
	};
	
	public static final TimeSeriesFilter ALL_TEMPERATURES = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Temperatures";
		}
		
		@Override
		public String id() {
			return "temperatures_all";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				return parent instanceof TemperatureResource;
			}
			if (!(schedule instanceof RecordedData))
				return false;
			final String id = ((RecordedData) schedule).getPath();
			String[] components = id.split("/");
			int length = components.length;
			if (length < 2)
				return false;
			String last = components[length-1];
			String secondLast = components[length-2].toLowerCase();
			switch (last) {
			case "reading":
				return secondLast.contains("temperature");
			case "setpoint":
				if (length < 3 || (!secondLast.equals("settings") && !secondLast.equals("deviceFeedback")))
					return false;
				return components[length-3].toLowerCase().contains("temperature");
			default:
				return false;
			}
		}
	};
	
	
	public static final TimeSeriesFilter THERMOSTAT_SETPOINTS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Thermostat setpoints";
		}
		
		@Override
		public String id() {
			return "thermostat_setpoints";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				if (!(parent instanceof TemperatureResource) || !parent.getName().equals("setpoint"))
					return false;
				while (parent != null) {
					parent = parent.getParent();
					if (parent instanceof Thermostat)
						break;
				}
				if (parent == null)
					return false;
				return ((Schedule) schedule).equalsLocation(((Thermostat) parent).temperatureSensor().deviceFeedback().setpoint());
			}
			else if (schedule instanceof RecordedData) {
				final String id = ((RecordedData) schedule).getPath();
				String[] components = id.split("/");
				int length = components.length;
				if (length < 2)
					return false;
				String last = components[length-1];
				String secondLast = components[length-2].toLowerCase();
				switch (last) {
				case "setpoint":
					if (length < 3 || !secondLast.equals("deviceFeedback"))
						return false;
					return components[length-3].toLowerCase().contains("temperature");
				default:
					return false;
				}
			}
			else 
				return false;
		}
	};
	
	public static final TimeSeriesFilter TEMPERATURE_MANAGEMENT_SETPOINTS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Temperature management setpoints";
		}
		
		@Override
		public String id() {
			return "temperature_mgmt_setpoints";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				if (!(parent instanceof TemperatureResource) || !parent.getName().equals("setpoint"))
					return false;
				while (parent != null) {
					parent = parent.getParent();
					if (parent instanceof Thermostat)
						break;
				}
				if (parent == null)
					return false;
				return ((Schedule) schedule).equalsLocation(((Thermostat) parent).temperatureSensor().settings().setpoint());
			}
			else if (schedule instanceof RecordedData) {
				final String id = ((RecordedData) schedule).getPath();
				String[] components = id.split("/");
				int length = components.length;
				if (length < 2)
					return false;
				String last = components[length-1];
				String secondLast = components[length-2].toLowerCase();
				switch (last) {
				case "setpoint":
					if (length < 3 || !secondLast.equals("settings"))
						return false;
					return components[length-3].toLowerCase().contains("temperature");
				default:
					return false;
				}
			}
			else 
				return false;
		}
	};
	
	public static final TimeSeriesFilter ALL_HUMIDITIES = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Humidities";
		}
		
		@Override
		public String id() {
			return "humidity_all";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				// XXX there is no HumidityResource type yet
				if (parent != null && parent.getParent() instanceof HumiditySensor)
					return true;
				else 
					return false;
			}
			if (!(schedule instanceof RecordedData))
				return false;
			final String id= ((RecordedData) schedule).getPath();
			String[] components = id.split("/");
			int length = components.length;
			if (length < 2)
				return false;
			String last = components[length-1];
			String secondLast = components[length-2].toLowerCase();
			switch (last) {
			case "reading":
				return secondLast.contains("humidity");
			default:
				return false;
			}
		}
	};
	
	public static final TimeSeriesFilter TEMPERATURE_MEASUREMENTS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Temperature measurements";
		}
		
		@Override
		public String id() {
			return "temperatures_measurements";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (!(schedule instanceof RecordedData))
				return false;
			final String id = ((RecordedData) schedule).getPath();
			String[] components = id.split("/");
			int length = components.length;
			if (length < 2)
				return false;
			String last = components[length-1];
			String secondLast = components[length-2].toLowerCase();
			switch (last) {
			case "reading":
				return secondLast.contains("temperature");
			default:
				return false;
			}
		}
	};
	
	public static final TimeSeriesFilter HUMIDITY_MEASUREMENTS = new TimeSeriesFilter() {
		
		@Override
		public String label(OgemaLocale arg0) {
			return "Humidity measurements";
		}
		
		@Override
		public String id() {
			return "humidity_measurements";
		}
		
		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (!(schedule instanceof RecordedData))
				return false;
			final String id= ((RecordedData) schedule).getPath();
			String[] components = id.split("/");
			int length = components.length;
			if (length < 2)
				return false;
			String last = components[length-1];
			String secondLast = components[length-2].toLowerCase();
			switch (last) {
			case "reading":
				return secondLast.contains("humidity");
			default:
				return false;
			}
		}
	};
	

	
	public static final TimeSeriesFilter VALVE_POSITIONS = new TimeSeriesFilter() {

		@Override
		public String label(OgemaLocale arg0) {
			return "Thermostat valve positions";
		}

		@Override
		public String id() {
			return "thermostat_valve_positions";
		}

		@Override
		public boolean accept(ReadOnlyTimeSeries schedule) {
			if (schedule instanceof Schedule) {
				Resource parent = ((Schedule) schedule).getParent();
				if (!(parent instanceof FloatResource) || !parent.getName().equals("stateFeedback"))
					return false;
				while (parent != null) {
					parent = parent.getParent();
					if (parent instanceof Thermostat)
						break;
				}
				if (parent == null)
					return false;
				return ((Schedule) schedule).equalsLocation(((Thermostat) parent).valve().setting().stateFeedback());
			} else if (schedule instanceof RecordedData) {
				final String path = ((RecordedData) schedule).getPath();
				String[] components = path.split("/");
				int length = components.length;
				if (length < 2)
					return false;
				String last = components[length - 1];
				String secondLast = components[length - 2].toLowerCase();
				switch (last) {
				case "setpoint":
					if (length < 3 || !secondLast.equals("setting"))
						return false;
					return components[length - 3].toLowerCase().contains("valve");
				default:
					return false;
				}
			} else
				return false;
		}
	};

}
