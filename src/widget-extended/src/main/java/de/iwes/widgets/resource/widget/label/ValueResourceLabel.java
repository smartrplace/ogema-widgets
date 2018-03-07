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