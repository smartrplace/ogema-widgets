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
package de.iwes.widgets.api.extended.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.PageRegistration;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.start.JsBundleApp;

public class Session {
	
	private volatile long lastUpdateTime;
	private volatile long expiryTime = Constants.SESSION_EXPIRY_TIME;
	private final ExecutorService exec;
	// Map<widgetID, widget data>; for all non-global widgets
	private final ConcurrentMap<String, WidgetData> widgets = new ConcurrentHashMap<>();
	// Map<widgetID, widget>; only for session widgets
	private final ConcurrentMap<String, ConfiguredWidget<?>> sessionWidgets = new ConcurrentHashMap<>();
	// unmodifiable
	private final Map<String,String[]> pageParameters; 
	private final PageRegistration page;
	
	public Session(PageRegistration page, Map<String,String[]> params) {
		this.page = page;
		if (params == null)
			this.pageParameters = Collections.emptyMap();
		else
			this.pageParameters = Collections.unmodifiableMap(params);
		this.exec = Executors.newSingleThreadExecutor(WidgetThreadFactory.getInstance());
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}
	
	@SuppressWarnings("unchecked")
	public final <T extends WidgetData> T getWidgetData(OgemaWidget widget) {
		String widgetId = widget.getId();
		T opt = (T) widgets.get(widgetId);
		if (opt != null)
			return opt;
		opt = ((OgemaWidgetBase<T>) widget).createNewSession();
		widgets.putIfAbsent(widgetId, opt);
		return (T) widgets.get(widgetId);
	}
	
	void close() {
		if (!this.exec.isShutdown()){
			try {
				this.exec.shutdownNow();
			} catch (Exception ignore) {}
		}
		//TODO destroy session specific widgets? make sure not to reanimate this object!
		// probably not necessary
	}
	
	public final void removeWidget(String widgetId) {
		final WidgetData data = widgets.remove(widgetId);
		if (data != null) {
			try {
				data.destroy();
			} catch (Exception e) {
				LoggerFactory.getLogger(JsBundleApp.class).error("Failed to delete widget session data",e);
			}
		}
		// FIXME ok, or do we need to delete the widget? Should only be referenced here anyway. Calling
		// destroyWidget is dangerous, (?)
		sessionWidgets.remove(widgetId);  // no further reference will be held to the session widget
	}
	
	/**
	 * @return
	 * 		a live view of the session widgets
	 */
	public final Map<String,ConfiguredWidget<?>> getSessionWidgets() {
		return sessionWidgets;
	}
	
	public final void addSessionWidget(ConfiguredWidget<?> w) {
		String widgetId = w.widget.getId();
		if (sessionWidgets.containsKey(widgetId))
			throw new IllegalArgumentException("Widget " + widgetId + " already exists");
		sessionWidgets.put(widgetId, w);
	}
	
	public final ConfiguredWidget<?> getSessionWidget(String widgetId) {
		return sessionWidgets.get(widgetId);
	}
	
	final PageRegistration getPage() {
		return page;
	}

	final void setExpiryTime(long expiryTime) {
		if (expiryTime <= 0)
			throw new IllegalArgumentException("Expiry time must be positive: " + expiryTime);
		this.expiryTime = expiryTime;
	}
	
	public final void setLastInteractionTime(long time) {
		this.lastUpdateTime = time;
	}
	
	final long getLastInteractionTime() {
		return lastUpdateTime;
	}
	
	final long getExpiryTime() {
		return expiryTime;
	}
	
	final ExecutorService getExecutor() {
		return exec;
	}
	
	/**
	 * @return
	 * 		returns an unmodifiable map
	 */
	final Map<String,String[]> getPageParameters() {
		return pageParameters;
	}

}
