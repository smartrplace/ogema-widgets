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
package de.iwes.tools.apps.collections.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ogema.core.administration.AdminApplication;
import org.ogema.core.administration.AdministrationManager;
import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import de.iwes.tools.apps.collections.api.DisplayableApp;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

public abstract class AppCollection<A extends DisplayableApp> {
	
	protected final Map<String, A> registeredApps = new ConcurrentHashMap<>(); 
	protected final Map<String,AdminApplication> validApps = Collections.synchronizedMap(new LinkedHashMap<String,AdminApplication>());
	protected final WidgetApp wapp;
	// only set when app is running
	protected final Logger logger;
	protected final ApplicationManager am;
	private volatile boolean staticAppsComplete = false;
	private final WidgetPage<?> page;
	
	public AppCollection(ApplicationManager am, WidgetApp app) {
		this(am, app, "index.html", true);
	}
	
	/**
	 * Generic constructor
	 * @param am
	 * @param app
	 * @param pageUrl
	 * @param setAsStartPage
	 */
	public AppCollection(ApplicationManager am, WidgetApp app, String pageUrl, boolean setAsStartPage) {
		this.am = am;
		this.logger = am.getLogger();
		logger.info("App collection view available: {}",getClass().getName());
		staticAppsComplete = addApps(staticApps());
		if (!registeredApps.isEmpty()) {
			final List<String> keys = new ArrayList<>(registeredApps.keySet());
			String[] arr = new String[keys.size()];
			addApps(keys.toArray(arr));
		}
		this.wapp = app;
		this.page = wapp.createWidgetPage(pageUrl, setAsStartPage);
		new MainPage<A>(page, this);
	}
	
	/*
	 * Methods to be overridden 
	 */
	protected abstract String pageTitle();
	
	/**
	 * @return
	 * 		a map with keys: bundle id, values: description  
	 */
	protected abstract Map<String,String> staticApps();

	public void addApp(A app) {
		String id = app.bundleSymbolicName();
		if (id == null || id.trim().isEmpty()) {
			logger.warn("AdminApp with invalid id: {}",app);
			return;
		}
		id = id.trim();
		// TODO filter out invalid ids or those that already exist
		registeredApps.put(id, app);
		addApps(id);
	}
	
	public void removeApp(A app) {
		String id = app.bundleSymbolicName();
		if (id == null || id.trim().isEmpty()) {
			logger.warn("AdminApp with invalid id: {}",app);
			return;
		}
		id = id.trim();
		// TODO filter out invalid ids or those that already exist
		registeredApps.remove(id);
		validApps.remove(id);
	}
	
	protected final Collection<AdminApplication> getApps() {
		if (!staticAppsComplete)
			addApps(staticApps());
		final Collection<AdminApplication> result;
		synchronized (validApps) {
			result = new ArrayList<>(validApps.values());
		}
		return result;
	}
	
	/**
	 * 
	 * @param apps	
	 * 	Map<Bundle symbolic name, description>
	 * @return
	 */
	protected boolean addApps(final Map<String,String> apps) {
		if (logger.isDebugEnabled())
			logger.debug("Trying to add apps: {}",apps.keySet());
		final AdministrationManager admin = am.getAdministrationManager();
		final List<AdminApplication> allApps = admin.getAllApps();
		final List<String> foundApps = new ArrayList<>();
		synchronized (validApps) {
			for (Map.Entry<String, String> entry : apps.entrySet()) {
				final String bsn = entry.getKey();
				AdminApplication a = null;
				for (AdminApplication aa : allApps) {
					final String sn = getSymbolicName(aa);
					if (sn == null || validApps.containsKey(sn))
						continue;
					if (sn.equals(bsn)) {
						a=aa;
						if (aa.getWebAccess().getStartUrl() != null) // otherwise continue searching; there may be multiple apps per bundle
							break;
					}
				}
				if (a == null)
					continue;
				validApps.put(bsn, a);
				foundApps.add(bsn);
			}
		}
		boolean success = foundApps.size() == apps.size();
		if (!success)
			logger.debug("Found only {} out of {} apps",foundApps.size(),apps.size());
		return success;
	}
	
	protected boolean addApps(String... bundleSymbolicNames) {
		if (logger.isDebugEnabled())
			logger.debug("Trying to add apps: {}", Arrays.toString(bundleSymbolicNames));
		final AdministrationManager admin = am.getAdministrationManager();
		final List<AdminApplication> allApps = admin.getAllApps();
		final List<String> foundApps = new ArrayList<>();
		synchronized (validApps) {
			for (String bsn : bundleSymbolicNames) {
				AdminApplication a  = null;
				for (AdminApplication aa : allApps) {
					final String sn = getSymbolicName(aa);
					if (sn == null || validApps.containsKey(sn))
						continue;
					if (bsn.equals(sn)) {
						a = aa;
						if (aa.getWebAccess().getStartUrl() != null) // otherwise continue searching; there may be multiple apps per bundle
							break;
					}
				}
				if (a == null)
					continue;
				validApps.put(bsn, a);
				foundApps.add(bsn);
			}
		}
		boolean success = foundApps.size() == bundleSymbolicNames.length;
		if (!success)
			logger.debug("Found only {} out of {} apps",foundApps.size(),bundleSymbolicNames.length);
		return success;
	}
	
	public WidgetPage<?> getPage() {
		return page;
	}
	
	protected static String getSymbolicName(AdminApplication a) {
		final Bundle b = a.getBundleRef();
		if (b == null)
			return null;
		return b.getSymbolicName();
	}
	
}
