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
package de.iwes.tools.apps.collections.base;

import java.security.AccessController;
import java.security.PrivilegedAction;
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
		this(am, app, "index.html", true, false);
	}
	
	/**
	 * Generic constructor
	 * @param am
	 * @param app
	 * @param pageUrl
	 * @param setAsStartPage
	 */
	public AppCollection(ApplicationManager am, WidgetApp app, String pageUrl, boolean setAsStartPage) {
		this(am, app, pageUrl, setAsStartPage, true);
	}
	public AppCollection(ApplicationManager am, WidgetApp app, String pageUrl, boolean setAsStartPage,
			boolean languageSelectionVisible) {
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
		page.getMenuConfiguration().setLanguageSelectionVisible(languageSelectionVisible);
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
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

			@Override
			public Boolean run() {
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
		});
		
	}
	
	protected boolean addApps(String... bundleSymbolicNames) {
		if (logger.isDebugEnabled())
			logger.debug("Trying to add apps: {}", Arrays.toString(bundleSymbolicNames));
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

			@Override
			public Boolean run() {
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
		});
		
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
