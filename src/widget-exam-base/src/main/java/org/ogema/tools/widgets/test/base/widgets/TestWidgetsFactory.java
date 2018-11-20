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
package org.ogema.tools.widgets.test.base.widgets;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.ogema.tools.widgets.test.base.GenericWidget;
import org.ogema.tools.widgets.test.base.WidgetLoader;

import de.iwes.widgets.api.extended.OgemaWidgetBase;

public class TestWidgetsFactory {
	
	private static final Map<String, Class<? extends GenericWidget>> knownTypes = new ConcurrentHashMap<>();
	
	/**
	 * called client-side
	 * @param widgetInfo
	 * @param loader
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static GenericWidget create(JSONArray widgetInfo, WidgetLoader loader) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String id= widgetInfo.getString(0);
		String type  =widgetInfo.getString(1);
		String servlet = widgetInfo.getString(2);
		GenericWidget widget;
		Class<? extends GenericWidget> clz = knownTypes.get(type);
		if (clz == null) {
//			System.out.println("Widget type " + type + " not registered");
			clz = GenericWidget.class;
		}
		widget = clz.getConstructor(WidgetLoader.class,String.class,String.class).newInstance(loader, id, servlet);
		return widget;
	}
	
	/**
	 * called by server-side widgets
	 * @param widget
	 */
	public static void registerType(Class<? extends OgemaWidgetBase<?>> serverWidgetType, Class<? extends GenericWidget> clientWidgetType) {
		knownTypes.put(serverWidgetType.getSimpleName(), clientWidgetType);
	}
	
	public static Class<? extends GenericWidget> getClientWidgetType(Class<? extends OgemaWidgetBase<?>> serverWidgetType) {
		Class<? extends GenericWidget> clz = knownTypes.get(serverWidgetType.getSimpleName());
		if (clz == null)
			clz = GenericWidget.class;
		return clz;
	}
 

}
