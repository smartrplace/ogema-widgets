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

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.InterpolationMode;

import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class TimeSeriesDataOnlineImpl implements TimeSeriesDataOnline {
	
	private final SingleValueResource resource;
	private final String label;
	private final String description;
	private final InterpolationMode mode;
	private final float offset;
	private final float factor;
	
	public TimeSeriesDataOnlineImpl(SingleValueResource resource, String label, String description, InterpolationMode mode) {
		this(resource, label, description, mode, 0, 0);
	}
	
	public TimeSeriesDataOnlineImpl(SingleValueResource resource, String label, String description, InterpolationMode mode,
			float offset, float factor) {
		Objects.requireNonNull(resource);
		this.resource = resource;
		this.label = label;
		this.description = description;
		this.mode = mode;
		this.offset = offset;
		this.factor = factor;
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
	public SingleValueResource getResource() {
		return resource;
	}
}
