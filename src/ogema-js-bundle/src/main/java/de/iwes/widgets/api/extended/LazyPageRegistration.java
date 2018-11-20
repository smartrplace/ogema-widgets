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
package de.iwes.widgets.api.extended;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ogema.accesscontrol.AccessManager;

import de.iwes.widgets.api.extended.impl.OgemaOsgiWidgetServiceImpl;
import de.iwes.widgets.api.extended.impl.SessionManagement;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;

@SuppressWarnings("rawtypes")
public class LazyPageRegistration extends PageRegistrationI {

	private static final long serialVersionUID = 1L;
	// sessions for this page will hold a strong reference to the delegate!
	private volatile WeakReference<PageRegistration> delegate = new WeakReference<PageRegistration>(null);
	private final LazyPage page;
	private final WidgetAppImpl app;
	
	public LazyPageRegistration(LazyPage page, SessionManagement sessions, AccessManager accMan, WidgetAppImpl app) {
		super(sessions, accMan, page);
		this.app = app;
		this.page= page;
	}

	/**
	 * Initializes the page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageRegistration getOrCreateRegistration() {
		PageRegistration master = delegate.get();
		if (master != null) 
			return master;
		synchronized (this) {
			master = delegate.get();
			if (master != null)
				return master;
			master = new PageRegistration(page, sessionManagement, accessManager);
			delegate = new WeakReference<PageRegistration>(master);
//			final ReferencePage page = new ReferencePage(app, this.page.getUrl());
			final WidgetPage<?> page = createPage(this.page.pageType, app, this.page.getUrl());
			final MenuConfiguration menu = this.page.getMenuConfiguration();
			if (menu != null) {
				page.getMenuConfiguration().setCustomNavigation(menu.getCustomNavigation());
			}
			try {
				// FIXME hacky... but we need to init the page reference before creating the page here
				final Field field = PageRegistrationI.class.getDeclaredField("page");
				field.setAccessible(true);
				field.set(master, page);
				this.page.getPageListener().accept(page);
			} catch (Exception e) {
				OgemaOsgiWidgetServiceImpl.logger.error("Page creation failed",e);
			}
			master.parameterAccessCounters = this.parameterAccessCounters; // ok to use the same reference here
			master.accessCountPersistent = this.accessCountPersistent;
			master.accessCountVolatile = this.accessCountVolatile;
			return master;
		}
	}
	
	public PageRegistration getRegistration() {
		return delegate.get();
	}
	
	@Override
	public WidgetGroupDerived getGroup(String id) {
		final PageRegistration master = delegate.get();
		return master == null ? null : master.getGroup(id);
	}

	@Override
	public WidgetGroupDerived removeGroup(String id) {
		final PageRegistration master = delegate.get();
		return master == null ? null : master.removeGroup(id);
	}

	@Override
	public void addGroup(WidgetGroupDerived group) {
		final PageRegistration master = delegate.get();
		if (master !=null)
			master.addGroup(group);
	}

	@Override
	public void close() {
		final PageRegistration master = delegate.get();
		if (master !=null)
			master.close();
	}
	
	@Override
	public ConfiguredWidget<?> getConfiguredWidget(OgemaWidgetBase<?> widget) {
		final PageRegistration master = delegate.get();
		return master == null ? null : master.getConfiguredWidget(widget);
	}
	
	@Override
	public ConfiguredWidget<?> getConfiguredWidget(String id, String sessionId) {
		final PageRegistration master = delegate.get();
		return master == null ? null : master.getConfiguredWidget(id, sessionId);
	}
	
	@Override
	public void addWidget(ConfiguredWidget<?> cw) {
		final PageRegistration master = delegate.get();
		if (master != null)
			master.addWidget(cw);
	}

	@Override
	public ConfiguredWidget<?> removeWidget(OgemaWidgetBase<?> widget) {
		final PageRegistration master = delegate.get();
		return master == null ? null : master.removeWidget(widget);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final PageRegistration master = delegate.get();
		if (master !=null)
			master.doGet(req, resp);
		else
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final PageRegistration master = delegate.get();
		if (master !=null)
			master.doPost(req, resp);
		else
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
	
	/*
	static class ReferencePage extends WidgetPageImpl {

		public ReferencePage(WidgetAppImpl app, String url) {
			super(app, url);
		}
		
	}
	*/
	
	private static WidgetPage<?> createPage(final Class<? extends WidgetPage> pageType, final WidgetAppImpl wapp, final String url) {
		if (pageType == null || pageType == WidgetPage.class)
			return new WidgetPageImpl<>(wapp, url);
		return AccessController.doPrivileged(new PrivilegedAction<WidgetPage<?>>() {

			@Override
			public WidgetPage<?> run() {
				try {
					final Constructor<?> constructor = pageType.getDeclaredConstructor(WidgetAppImpl.class, String.class);
					constructor.setAccessible(true);
					return (WidgetPage<?>) constructor.newInstance(wapp, url);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
}
