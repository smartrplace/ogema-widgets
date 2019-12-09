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
package de.iwes.timeseries.eval.base.provider.utils;

import java.util.Objects;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class TimeSeriesDataImpl implements TimeSeriesDataOffline {
	
	private final ReadOnlyTimeSeries timeSeries;
	private String label;
	private String description;
	private final InterpolationMode mode;
	private final float offset;
	private final float factor;
	private final long timeOffset;
	
	public TimeSeriesDataImpl(ReadOnlyTimeSeries timeSeries, String label, String description, InterpolationMode mode) {
		this(timeSeries, label, description, mode, 0, 0, 0);
	}
	
	public TimeSeriesDataImpl(ReadOnlyTimeSeries timeSeries, String label, String description, InterpolationMode mode,
			float offset, float factor, long timeOffset) {
		Objects.requireNonNull(timeSeries);
		this.timeSeries = timeSeries;
		this.label = label;
		this.description = description;
		this.mode = mode != null ? mode : timeSeries.getInterpolationMode();
		this.offset = offset;
		this.factor = factor;
		this.timeOffset= timeOffset;
	}

	public TimeSeriesDataImpl(TimeSeriesDataImpl base, String label, String description) {
		this.timeSeries = base.timeSeries;
		this.label = label;
		this.description = description;
		this.mode =  base.mode != null ?  base.mode : timeSeries.getInterpolationMode();
		this.offset =  base.offset;
		this.factor =  base.factor;
		this.timeOffset=  base.timeOffset;		
	}
	
	@Override
	public ReadOnlyTimeSeries getTimeSeries() {
		return timeSeries;
	}
	
	@Override
	public String id() {
		return label(OgemaLocale.ENGLISH);
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public String description(OgemaLocale locale) {
		return description;
	}

	@Override
	public InterpolationMode interpolationMode() {
		return mode;
	}

	@Override
	public float offset() {
		return offset;
	}

	@Override
	public float factor() {
		return factor;
	}

	@Override
	public long timeOffset() {
		return timeOffset;
	}
	
	@Override
	public String toString() {
		return "TimeSeriesDataImpl[" + label + "]";
	}
	
}
