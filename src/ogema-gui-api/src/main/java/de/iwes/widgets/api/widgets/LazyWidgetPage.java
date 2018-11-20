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
package de.iwes.widgets.api.widgets;

import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;

/**
 * Register as OSGi service, with properties
 * <ul>
 * 	 <li>"org.ogema.widgets.page.baseurl" (should be the same value for all pages in one bundle)
 *   <li>"org.ogema.widgets.page.pageurl" (page-specific url relative to baseUrl, e.g. "index.html")
 * </ul>
 */
// experimental API, do not use in production
public interface LazyWidgetPage {

	/**
	 * App base url, e.g. "/com/example/test/app"
	 */
	public static final String BASE_URL = "org.ogema.widgets.page.baseurl";
	/**
	 * Page url relative to base url, e.g. "index.html"
	 */
	public static final String RELATIVE_URL = "org.ogema.widgets.page.pageurl";
	
	/**
	 * Optional; a human readable menu entry, no more than 20 characters. E.g. "Test page".
	 */
	public static final String MENU_ENTRY = "org.ogema.widgets.page.menuentry";
	/**
	 * Optional, a boolean: "true" or "false" (default)
	 */
	public static final String START_PAGE = "org.ogema.widgets.page.startpage";
	
	public static final String CUSTOM_PAGE_TYPE = "org.ogema.widgets.page.type.custom"; // true or false
	
	void init(ApplicationManager appMan, WidgetPage<?> page);
	
	/**
	 * Override to use a non-standard {@link WidgetPage} type.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	default Class<? extends WidgetPage> getPageType() {
		return WidgetPage.class;
	}
	
	/*
	 ***** Stuff below is not part of the API... only required to get the right protection domain
	 */
	
	/**
	 * For internal use
	 */
	public static class AppDelegate implements Application {
		
		private final Application delegate;
		
		public AppDelegate(Application delegate) {
			this.delegate = delegate;
		}

		@Override
		public void start(ApplicationManager appManager) {
			delegate.start(appManager);
		}

		@Override
		public void stop(AppStopReason reason) {
			delegate.stop(reason);
		}
		
	}
	
}

