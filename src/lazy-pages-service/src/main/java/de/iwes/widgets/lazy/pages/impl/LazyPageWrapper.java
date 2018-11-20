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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

class LazyPageWrapper implements Consumer<WidgetPage<LocaleDictionary>> {
	
	private final ServiceReference<LazyWidgetPage> ref;
	private final String baseUrl;
	private final String relativeUrl;
	private final boolean isStartPage;
	private final String menuEntry;
	private final Bundle b;
	private volatile LazyWidgetPage page;
	volatile ApplicationManager appMan;

	LazyPageWrapper(ServiceReference<LazyWidgetPage> ref) {
		this.b = ref.getBundle();
		this.ref = ref;
		this.baseUrl = (String) ref.getProperty(LazyWidgetPage.BASE_URL);
		this.relativeUrl = (String) ref.getProperty(LazyWidgetPage.RELATIVE_URL);
		if (baseUrl == null || relativeUrl == null)
			throw new IllegalArgumentException("Urls must not be null: " + baseUrl + ", " + relativeUrl);
		try {
			new URL("http://localhost" + baseUrl + relativeUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL: " + baseUrl + relativeUrl);
		}
		final Object startPageProp = ref.getProperty(LazyWidgetPage.START_PAGE);
		this.isStartPage = startPageProp instanceof Boolean ? ((Boolean) startPageProp).booleanValue() :
				startPageProp instanceof String ? Boolean.parseBoolean((String) startPageProp) : false;
		String menu = (String) ref.getProperty(LazyWidgetPage.MENU_ENTRY);
		if (menu == null)
			menu = relativeUrl;
		if (menu.length() > 20)
			menu = menu.substring(0, 20);
		this.menuEntry = menu;
	}

	public ServiceReference<LazyWidgetPage> getRef() {
		return ref;
	}

	@SuppressWarnings("rawtypes")
	public Class<? extends WidgetPage> getPageType() {
		final String customProp = (String) ref.getProperty(LazyWidgetPage.CUSTOM_PAGE_TYPE);
		if (customProp == null)
			return WidgetPage.class;
		final BundleContext ctx = b.getBundleContext();
		final LazyWidgetPage page = ctx.getService(ref);
		try {
			return page.getPageType();
		} finally {
			ctx.ungetService(ref);
		}
		
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public String getRelativeUrl() {
		return relativeUrl;
	}
	
	public Bundle getBundle() {
		return b;
	}
	
	public boolean isStartPage() {
		return isStartPage;
	}
	
	public String getMenuEntry() {
		return menuEntry;
	}
	
	public void close() {
		final LazyWidgetPage page = this.page;
		this.page = null;
		this.appMan = null;
		if (page != null) {
			try {
				ref.getBundle().getBundleContext().ungetService(ref);
			} catch (Exception ignore) {}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof LazyPageWrapper))
			return false;
		final LazyPageWrapper other = (LazyPageWrapper) obj;
		return Objects.equals(b, other.b) && Objects.equals(relativeUrl, other.relativeUrl);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(relativeUrl, b);
	}

	@Override
	public void accept(final WidgetPage<LocaleDictionary> t) {
		final ApplicationManager appMan = this.appMan;
		if (appMan == null)
			return;
		LazyWidgetPage page = this.page;
		if (page == null) {
			synchronized (this) {
				page = this.page;
				if (page == null) {
					page= ref.getBundle().getBundleContext().getService(ref);
					this.page = page;
				}
			}
		}
		final LazyWidgetPage page1 = page;
		// page initialisation with full app rights; 
		// if we used the initial thread here this could lead to reduced resource permissions
		// furthermore, exceptions may arise in the init method
		final Thread init = new Thread(new Runnable() {
			
			@Override
			public void run() {
				page1.init(appMan, t);
			}
		}, "lazy-page-init");
		init.start();
		try {
			init.join();
		} catch (InterruptedException e) {
			try {
				init.interrupt();
			} catch (SecurityException ee) {
				appMan.getLogger().error("Failed to interrupt init thread",ee);
			}
			Thread.currentThread().interrupt();
		}
	}
	
}
