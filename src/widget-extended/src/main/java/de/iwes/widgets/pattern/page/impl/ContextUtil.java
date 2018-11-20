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
