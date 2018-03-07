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
	 * {@link #createWidgetApp(String, String, ApplicationManager, boolean)} 
	 * to change the default behaviour.
	 * @param appId
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
	 * @param pageSpecficId
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
