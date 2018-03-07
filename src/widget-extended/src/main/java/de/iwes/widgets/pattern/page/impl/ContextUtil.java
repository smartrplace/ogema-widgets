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
package de.iwes.widgets.pattern.page.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.pattern.ResourcePattern.CreateMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern.Existence;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.textfield.TextField;

public class ContextUtil {
	
	public static Object createContext(Class<?> contextType, Field[] fields, Map<String,OgemaWidgetBase<?>> widgets, ApplicationManager am, OgemaHttpRequest req) throws InstantiationException, IllegalAccessException {
		Object context = contextType.newInstance();
		Class<?> type;
		Object value;
		OgemaWidgetBase<?> widget;
		for (Field f : fields) {
			f.setAccessible(true);
			widget = widgets.get(f.getName());
			if (widget == null)
				throw new NullPointerException("Widget corresponding to field " + f.getName() + " not found");
			value = getValue(widget,am.getResourceAccess(),req);
			type = f.getType();
			if (value instanceof String)
				value = convert((String) value, type);
			if (value == null) {
				Existence e = f.getAnnotation(Existence.class);
				if (e == null || e.required() == CreateMode.MUST_EXIST) 
					throw new NullPointerException("Field value " + f.getName() + " is null");
			}
			f.set(context, value);
		}
		return context;
	}
	
	
	private static Object getValue(OgemaWidgetBase<?> widget, ResourceAccess ra, OgemaHttpRequest req) {
		if (widget instanceof TextField) 
			return ((TextField) widget).getValue(req).trim();
		if (widget instanceof ReferenceDropdown<?>) {
			String selected = ((ReferenceDropdown<?>) widget).getSelectedValue(req);
			if (selected == null || selected.equals(DropdownData.EMPTY_OPT_ID)) 
				return null;
			return ra.getResource(selected); // will need to create the associated pattern later on
		}
		else 
			throw new UnsupportedOperationException("Context creation for widget type " + widget.getWidgetClass().getSimpleName() + " not implemented yet");
	}
	
	private static Object convert(String value, Class<?> targetType) {
		if  (String.class.isAssignableFrom(targetType))
			return value;
		if (Integer.class.isAssignableFrom(targetType) || Integer.TYPE == targetType)
			return Integer.parseInt(value);
		if (Long.class.isAssignableFrom(targetType) || Long.TYPE == targetType) 
			return Long.parseLong(value);
		if (Float.class.isAssignableFrom(targetType) || Float.TYPE == targetType) 
			return Float.parseFloat(value);
		if(Double.class.isAssignableFrom(targetType) || Double.TYPE == targetType) 
			return Double.parseDouble(value);
		if (Boolean.class.isAssignableFrom(targetType) || Boolean.TYPE == targetType) 
			return Boolean.parseBoolean(value);
		throw new UnsupportedOperationException("Widget return type String does not match expected type " + targetType.getSimpleName());
	}
	
	
}
