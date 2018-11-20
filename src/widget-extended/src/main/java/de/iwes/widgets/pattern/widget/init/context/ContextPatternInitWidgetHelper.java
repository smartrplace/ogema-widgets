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
package de.iwes.widgets.pattern.widget.init.context;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

public class ContextPatternInitWidgetHelper {
	
	static <C> C createContext(final WidgetPage<?> page, final Class<C> type, final OgemaHttpRequest req) {
		return AccessController.doPrivileged(new PrivilegedAction<C>() {

			@Override
			public C run() {
				C instance;
				try {
					instance = type.getConstructor().newInstance();
				} catch (Exception e) {
					LoggerFactory.getLogger(ContextPatternInitWidgetHelper.class).warn("Could not create context instance",e);
					return null;
				} 
				Map<String,String[]> parameters = page.getPageParameters(req);
				for (Field f: type.getFields()) {
					String field = f.getName();
					try {
						String[] params = parameters.get(field);
						if (params == null || params.length == 0)
							continue;
						setValue(f, params, instance);
					} catch (Exception e) {
						LoggerFactory.getLogger(ContextPatternInitWidgetHelper.class).warn("Could not context field",e);
						continue;
					}
				}
				return instance;
			}
		});
		
	}
	
	private static final void setValue(Field field, String[] params, Object obj) throws IllegalAccessException {
		Class<?> type = field.getType();
		Object value = convert(params, type); // TODO check this works correctly
		field.set(obj, value);
	}
	
	static final String getSelected(WidgetPage<?> page, OgemaHttpRequest req) {
		Map<String,String[]> params = page.getPageParameters(req);
		if (params == null || params.isEmpty())
			return null;
		String[] patterns = params.get(TemplateRedirectButton.PAGE_CONFIG_PARAMETER);
		if (patterns == null || patterns.length == 0)
			return null;
		return patterns[0];
	}
	
	/**
	 * There is a corresponding method in common-beanutils; we try to avoid the dependency here
	 * @param params
	 * @param type
	 * @return
	 */
	public static final Object convert(String[] params,Class<?> type) {
		if (params == null || params.length == 0)
			return null;
		if (type.isArray()) {
			// TODO
			return params;
		}
		else if (type == String.class) {
			return params[0];
		}
		else if (type == Integer.class || type == int.class) 
			return Integer.parseInt(params[0]);
		else if (type == Long.class || type == long.class)
			return Long.parseLong(params[0]);
		else if (type == Boolean.class || type == boolean.class)
			return Boolean.parseBoolean(params[0]);
		else if (type == Float.class || type == float.class)
			return Float.parseFloat(params[0]);
		else if (type == Double.class || type == double.class) 
			return Double.parseDouble(params[0]);
		try {
			return type.getConstructor(String.class).newInstance(params[0]);
		} catch (Exception e)  { /* ignore... 't was worth a try */ }
		LoggerFactory.getLogger(ContextPatternInitWidgetHelper.class).error("Cannot convert item of type " + type.getName());
		return null;
	}

}
