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
package de.iwes.widgets.lazy.pages.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;

@References({
	@Reference(
			name="pages",
			referenceInterface=LazyWidgetPage.class,
			cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			policy=ReferencePolicy.DYNAMIC,
			bind="addPage",
			unbind="removePage"),
	@Reference(
			referenceInterface=OgemaGuiService.class,
			bind="addWidgetService",
			unbind="removeWidgetService",
			cardinality=ReferenceCardinality.MANDATORY_UNARY,
			policy=ReferencePolicy.STATIC
	)
})
@Component(immediate=true)
public class LazyPagesService {

	private volatile OgemaGuiService widgetService;
	
	protected void addWidgetService(OgemaGuiService widgetService) {
		final Map<Bundle,LazyPageWrapper> copy;
		synchronized (waiting) {
			this.widgetService = widgetService;
			copy  = new HashMap<>(waiting);
			waiting.clear();
		}
		for (Map.Entry<Bundle, LazyPageWrapper> entry : copy.entrySet()) {
			addApp1(entry.getKey(), entry.getValue().getBaseUrl(), entry.getValue());
		}
	}
	
	protected void removeWidgetService(OgemaGuiService widgetService) {
		this.widgetService = null;
	}
	
	// service references that we collect
	private final Set<LazyPageWrapper> pages = Collections.synchronizedSet(new HashSet<>());
	// widgets apps per bundle
	private final ConcurrentMap<Bundle, WidgetAppWrapper> apps = new ConcurrentHashMap<>();
	// these service references have been obtained before the widget service was available
	private final ConcurrentMap<Bundle,LazyPageWrapper> waiting = new ConcurrentHashMap<>(4);
	// bundles for which an app has been registered, but the start callback has not been issued yet
	private final Set<Bundle> pendingBundles = Collections.newSetFromMap(new ConcurrentHashMap<Bundle,Boolean>(8));
	// app registrations per bundle
	private final ConcurrentMap<Bundle, ServiceRegistration<Application>> appRegistrations = new ConcurrentHashMap<>();
	
	@Deactivate
	protected void deactivate() {
		for (WidgetAppWrapper app :apps.values()) {
			try {
				app.wapp.close();
			} catch (Exception ignore) {};
		}
		if (!appRegistrations.isEmpty()) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for (ServiceRegistration<Application> reg : appRegistrations.values()) {
						try {
							reg.unregister();
						} catch (Exception ignore) {}
					}
					appRegistrations.clear();
				}
			}).start();
		}
		apps.clear();
		pendingBundles.clear();
	}
	
	protected void addPage(ServiceReference<LazyWidgetPage> pageRef) {
		final Bundle b;
		final LazyPageWrapper wrapper = new LazyPageWrapper(pageRef);
		pages.add(wrapper);
		b = pageRef.getBundle();
		final WidgetAppWrapper app = apps.get(b);
		if (app != null) {
			try {
				wrapper.appMan = app.appMan; 
				app.wapp.createLazyPage(wrapper.getRelativeUrl(), wrapper, wrapper.getPageType(), wrapper.isStartPage());
				app.addingPage(wrapper);
			} catch (Exception e) {
				// TODO in rare cases it may happen that the page has already been registered by the starting app... catch this case
				e.printStackTrace();
			}
		} else {
			addApp(b, wrapper.getBaseUrl(), wrapper);
		}
	}
	
	protected void removePage(ServiceReference<LazyWidgetPage> pageRef) {
		pages.remove(new LazyPageWrapper(pageRef));
		final WidgetAppWrapper wapp = apps.get(pageRef.getBundle());
		if (wapp != null) {
			// TODO removing a page from an app does not seem to be foreseen
		}
	}
	
	private void addApp(final Bundle b, final String baseUrl, LazyPageWrapper wrapper) {
		if (pendingBundles.contains(b))
			return;
		synchronized (pendingBundles)  {
			if (pendingBundles.contains(b))
				return;
			pendingBundles.add(b);
		}
		OgemaGuiService widgetService = this.widgetService;
		if (widgetService != null) {
			addApp1(b, baseUrl, wrapper);
		} else {
			synchronized (waiting) {
				 widgetService = this.widgetService;
				 if (widgetService == null) {
					 waiting.put(b, wrapper);
					 return;
				 }
			}
			addApp1(b, baseUrl, wrapper);
		}
	}
	
	private void addApp1(final Bundle b, final String baseUrl, LazyPageWrapper wrapper) {
		final Application app = new WidgetAppApplication(baseUrl, b, pages, apps, widgetService, pendingBundles);
		final Application app1; 
		if (System.getSecurityManager() == null) {
			app1 = AppFactory.wrapNoSecurity(b, app); // we do not care about the protection domain of the app, but wrap the app to get a useful logger name
		} else {
			final ServiceReference<LazyWidgetPage> ref = wrapper.getRef();
			try {
				// we wrap the app in another one with the proper protection domain
				// this requires actually loading the service class
				app1 = AppFactory.wrap(b.getBundleContext().getService(ref).getClass(), app);
			} catch (Exception e) {
				throw new RuntimeException("App wrapping failed", e);
			}
		}
		appRegistrations.put(b, b.getBundleContext().registerService(Application.class, app1, null));
	}
	
	static class WidgetAppApplication implements Application {

		// synchronized set
		private final Set<LazyPageWrapper> pages; 
		private final ConcurrentMap<Bundle, WidgetAppWrapper> apps;
		private final Set<Bundle> pending;
		private final String baseUrl;
		private final Bundle b;
		private WidgetAppWrapper wrapp;
		private final OgemaGuiService widgetService;
		
		WidgetAppApplication(String baseUrl, Bundle b,  final Set<LazyPageWrapper> pages, 
				final ConcurrentMap<Bundle, WidgetAppWrapper> apps, OgemaGuiService widgetService,
				Set<Bundle> pending) {
			this.b = b;
			this.widgetService  =widgetService;
			this.baseUrl = baseUrl;
			this.pages = pages;
			this.apps = apps;
			this.pending = pending;
		}
		
		@Override
		public void start(ApplicationManager appManager) {
			final WidgetApp wapp = widgetService.createWidgetApp(baseUrl, appManager);
			this.wrapp = new WidgetAppWrapper(wapp, appManager);
			apps.put(b, wrapp);
			final List<LazyPageWrapper> wrappers;
			synchronized(pages) {
				wrappers = pages.stream()
					.filter(wrapper -> wrapper.getBundle().getBundleId() == b.getBundleId())
					.collect(Collectors.toList());
			}
			wrappers.forEach(wrapper -> {
				try {
					wrapper.appMan = appManager;
					wapp.createLazyPage(wrapper.getRelativeUrl(), wrapper, wrapper.getPageType(), wrapper.isStartPage());
					wrapp.addingPage(wrapper);
				} catch (Exception e) {
					appManager.getLogger().error("Failed to register page", e);
				}
			});
			pending.remove(b);
		}

		@Override
		public void stop(AppStopReason reason) {
			final WidgetAppWrapper wrapp = this.wrapp;
			this.wrapp = null;
			if (wrapp != null) {
				apps.remove(b, wrapp);
				wrapp.wapp.close();
			}
			synchronized (pages) {
				for (LazyPageWrapper wrapper : pages ) {
					if (b.equals(wrapper.getBundle())) {
						wrapper.appMan = null;
					}
				}
			}
			pending.remove(b);
		}
		
	}
	
	static class WidgetAppWrapper {
		
		final WidgetApp wapp;
		final ApplicationManager appMan;
		private final Map<String, String> menuEntries = new HashMap<>(4);
		
		WidgetAppWrapper(WidgetApp wapp, ApplicationManager appMan) {
			this.wapp = wapp;
			this.appMan = appMan;
		}
		
		void addingPage(LazyPageWrapper wrapper) {
			synchronized (menuEntries) {
				menuEntries.put(wrapper.getRelativeUrl(), wrapper.getMenuEntry());
				if (menuEntries.size() > 1) {
					final NavigationMenu menu = new NavigationMenu(" Select page");
					wapp.getPages().entrySet().forEach(entry -> menu.addEntry(menuEntries.get(entry.getKey()), entry.getValue()));
					// FIXME unexpected NPE
					wapp.getPages().values().forEach(page -> page.getMenuConfiguration().setCustomNavigation(menu));
				}
			}
		}
		
		// TODO call
		void removingPage(LazyPageWrapper wrapper) {
			synchronized (menuEntries) {
				menuEntries.remove(wrapper.getRelativeUrl());
				if (menuEntries.size() == 1) {
					wapp.getPages().values().forEach(page -> page.getMenuConfiguration().setCustomNavigation(null));
				}
			}
		}
		
		
	}
	
}
