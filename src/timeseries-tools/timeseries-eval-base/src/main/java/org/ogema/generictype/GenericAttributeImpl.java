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
package org.ogema.generictype;

import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class GenericAttributeImpl implements GenericAttribute {
	protected final String label;
	
	public GenericAttributeImpl(String label) {
		this.label = label;
	}

	@Override
	public String id() {
		return ResourceUtils.getValidResourceName(getClass().getName());
	}

	@Override
	public String label(OgemaLocale arg0) {
		return label;
	}

	public final static GenericAttribute OUTSIDE = new GenericAttributeImpl("Sensor or actor outside building(s)");
	public final static GenericAttribute SENSOR_SEPARATE = new GenericAttributeImpl(
			"Sensor is not part of actor device");
	public final static GenericAttribute SENSOR_WITH_ACTOR = new GenericAttributeImpl(
			"Sensor is part of actor device");
	public final static GenericAttribute MAIN_SENSOR = new GenericAttributeImpl("Sensor is top-level measurement (e.g. a grid-connection meter) and may have sub-measurements (e.g. sub-meters)");
	public final static GenericAttribute SUB_SENSOR = new GenericAttributeImpl("Sensor is a sub-measurement (e.g. a sub-meter)");
	public final static GenericAttribute TEST = new GenericAttributeImpl("Marked as test output, should only be used if no other suitable data is available");
}
