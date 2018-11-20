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
package de.iwes.widgets.api;

import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.services.IconService;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetApp;

/**
 * The central entry point to the widgets framework.
 * Retrieve an instance as an OSGi service.
 */
public interface OgemaGuiService {
	
	/**
	 * Create a new widget app. User sessions are page-specific, i.e. if a user opens
	 * a new browser tab, no information will be shared between tabs server-side. Use
	 * {@link #createWidgetApp(String, ApplicationManager, boolean)} 
	 * to change the default behaviour.
	 * 		A unique id for the new widget app
	 * @param url
	 * 		A common url prefix that will be added to the urls of all widget pages
	 * 		registered with this widget app. E.g. "/org/example/superapp"
	 * @param am
	 * @return
	 */
	public WidgetApp createWidgetApp(String url, ApplicationManager am);

	/**
	 * Create a new widget app. 
	 * @param url
	 * 		A common url prefix that will be added to the urls of all widget pages
	 * 		registered with this widget app. E.g. "/org/example/superapp"
	 * @param am
	 * @param pageSpecificId
	 * 		Set to false in order to share data between tabs server-side.
	 * @return
	 * @deprecated page specific id parameter no longer supported, 
	 * 		use {@link #createWidgetApp(String, ApplicationManager)} instead.
	 */
	@Deprecated
	public WidgetApp createWidgetApp(String url, ApplicationManager am, boolean pageSpecificId);

	/**
	 * The icon service provides icons for some OGEMA resource types.
	 * @return
	 * 		null, if no icon service provider is registered
	 */
	public IconService getIconService();
	
	/**
	 * The name service associates reader-friendly names to some OGEMA resources and resource types.
	 * @return
	 * 		null, if no name service provider is registered
	 */
	public NameService getNameService();
	
	/**
	 * Send messages to the user.
	 * @return
	 */
	public MessagingService getMessagingService();
	
}
