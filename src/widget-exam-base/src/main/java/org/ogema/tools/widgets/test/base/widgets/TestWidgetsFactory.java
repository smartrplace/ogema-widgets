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
