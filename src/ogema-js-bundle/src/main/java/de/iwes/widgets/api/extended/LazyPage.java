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
import java.lang.reflect.Method;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.iwes.widgets.api.extended.impl.OgemaOsgiWidgetServiceImpl;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;

public class LazyPage<S extends LocaleDictionary>  extends WidgetPageBase<S> implements ServletBasedWidgetPage<S> {

	private final HttpServlet initServlet;
	// the actual page, if it exists
	private final Consumer<WidgetPage<?>> pageListener;
	private final OgemaOsgiWidgetServiceImpl widgetService;
	private volatile MenuConfiguration menuConfiguration = new MenuConfiguration();
	final Class<? extends WidgetPage<?>> pageType;

	LazyPage(Consumer<WidgetPage<?>> pageListener, WidgetAppImpl app, String startHtml, 
			boolean setAsStartPage, Class<? extends WidgetPage<?>> pageType) {
		super(app, startHtml, setAsStartPage);
		this.pageListener = pageListener;
		this.pageType = pageType;
		this.widgetService = app.getWidgetService();
		this.initServlet = new LazyInitServlet(this);
	}

	public Consumer<WidgetPage<?>> getPageListener() {
		return pageListener;
	}

	@Override
	public HttpServlet getServlet() {
		return initServlet;
	}

	@Override
	public MenuConfiguration getMenuConfiguration() {
		return menuConfiguration;
	}
	
	@Override
	public void registerLibrary(HtmlLibrary lib) {
		throw new UnsupportedOperationException();
	}

	private static class LazyInitServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;
		private final LazyPage<?> page;

		LazyInitServlet(LazyPage<?> page) {
			this.page=  page;
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			final LazyPageRegistration lazyReg =  (LazyPageRegistration) page.widgetService.createPageRegistration(page, page.app.wam);
			final PageRegistration reg = lazyReg.getOrCreateRegistration();
			final HttpServlet servlet = ((ServletBasedWidgetPage<?>) reg.getPage()).getServlet();
			// servlet.doGet(req, resp);
			try {
				final Method get = HttpServlet.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
				get.setAccessible(true);
				get.invoke(servlet, req, resp);
			} catch (Exception e) {
				if (e instanceof ServletException)
					throw (ServletException) e;
				throw new ServletException(e);
			}
		}

	}

}
