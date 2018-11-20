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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.sim.roomsimservice.logic;

import java.util.List;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

public class GenericCalculations {

	private static GenericCalculations instance = null;
	
	private GenericCalculations() {
	}
	
	public static GenericCalculations getInstance() {
		if (instance == null) {
			instance = new GenericCalculations();
		}
		return instance;
	}
	
	// in unit/ms
	public float getGradient(SampledValue sv1, SampledValue sv2) {
		long tDiff = sv2.getTimestamp() - sv1.getTimestamp(); 
		float valDiff = sv2.getValue().getFloatValue() - sv1.getValue().getFloatValue();
		return valDiff / ((float) tDiff);
	}
	
	public float getAverage(List<SampledValue> values, long t0, long t1) {
		FloatTimeSeries mem = new FloatTreeTimeSeries();
		mem.addValues(values);
		mem.setInterpolationMode(InterpolationMode.LINEAR);
		return mem.integrate(t0, t1) / ((float)(t1-t0));		
	}
	
	public float getAverage(List<SampledValue> values) {
		if (values == null || values.isEmpty()) return Float.NaN;
		long t0 = 0;
		long t1 = Long.MAX_VALUE;
		int lastI = -1;
		for (int i=0;i<values.size(); i++) {
			if (t0 == 0) {
				SampledValue sv = values.get(0);
				if (sv.getQuality() == Quality.GOOD) {
					t0 = sv.getTimestamp();
				}
			}
			if (t1 == Long.MAX_VALUE) {
				SampledValue sv = values.get(values.size()-i-1);
				if (sv.getQuality() == Quality.GOOD) {
					t1 = sv.getTimestamp();
				}
			}
			lastI = i;
			if (t0 != 0 && t1 != Long.MAX_VALUE) break;
		}
		if (t0 == 0) return Float.NaN;
		else if (t0 == t1) {
			return values.get(lastI).getValue().getFloatValue();
		}
		return getAverage(values, t0, t1);
	}
	
	
}
