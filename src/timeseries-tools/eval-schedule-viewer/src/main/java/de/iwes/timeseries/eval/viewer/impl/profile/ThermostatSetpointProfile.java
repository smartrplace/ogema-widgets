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
package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesBuilder;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

class ThermostatSetpointProfile implements Profile {

	@Override
	public String id() {
		return "thermostatSetpointStd";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Thermostat setpoint";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Thermostat temperature setpoint";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return InterpolationMode.STEPS;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		Resource setpoint = null;
		if (schedule instanceof Schedule)
			setpoint = ((Schedule) schedule).getParent();
		else if (schedule instanceof OnlineTimeSeries)
			setpoint = ((OnlineTimeSeries) schedule).getResource();
		if (setpoint != null) {
			Resource parent=setpoint.getParent();
			if (!(parent instanceof TemperatureSensor))
				return false;
			return ((TemperatureSensor) parent).settings().setpoint().equalsLocation(setpoint);
		}
		if (!(schedule instanceof RecordedData))
			return false;
		final String id= ((RecordedData) schedule).getPath();
		String[] components = id.split("/");
		int length = components.length;
		if (length < 4)
			return false;
		if (!components[length-1].equals("setpoint") 
			|| !components[length-2].equals("settings")
			|| !components[length-3].equals("temperatureSensor"))
			return false;
		return components[length-4].toLowerCase().contains("thermostat");
	}

	// the average setpoint
	@Override
	public ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn) {
		if (constituents == null || constituents.isEmpty())
			return null;
		final ReadOnlyTimeSeries result;
		if (constituents.size() == 1) {
			result = constituents.iterator().next();
		}
		else {
			result = MultiTimeSeriesBuilder.newBuilder(constituents, Float.class)
				.setInterpolationModeForConstituents(InterpolationMode.LINEAR)
				.setInterpolationModeForResult(InterpolationMode.LINEAR)
				.setAverage()
				.ignoreGaps(true)
				.build();
		}
		final StringBuilder sb = new StringBuilder("Thermostat temperature setpoint");
		if (labelAddOn != null)
			sb.append(':').append(' ').append(labelAddOn);
		boolean online = false;
		for (ReadOnlyTimeSeries ts: constituents) {
			if (ts instanceof OnlineTimeSeries) {
				online = true;
				break;
			}
		}
		return new PresentationDataImpl(result, TemperatureResource.class, sb.toString(), InterpolationMode.LINEAR, this, online);	
	}
	
}
