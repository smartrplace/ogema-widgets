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
package de.iwes.widgets.resource.widget.label;

import java.util.Locale;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A label that simply prints the value of a {@link SingleValueResource}, such
 * as a {@link StringResource} or {@link TemperatureResource}. 
 * 
 * @see ResourceLabel
 * @see TimeResourceLabel
 *
 * @param <T>
 * 		The resource type. 
 */
public class ValueResourceLabel<T extends SingleValueResource> extends ResourceLabel<T> {

	private static final long serialVersionUID = 1L;

	public ValueResourceLabel(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public ValueResourceLabel(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	protected String format(T resource, Locale locale) {
		String output;
		if (resource instanceof FloatResource)
			output = ValueResourceUtils.getValue((FloatResource) resource, 2); // default; override in derived class, if necessary // FIXME or use parameter?
		else
			output = ValueResourceUtils.getValue(resource);
		return output;
	};

	
	
}