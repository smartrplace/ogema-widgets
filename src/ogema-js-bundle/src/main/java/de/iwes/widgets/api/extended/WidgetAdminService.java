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

package de.iwes.widgets.api.extended;

import java.util.List;
import java.util.Map;

import org.ogema.core.model.simple.IntegerResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

/**
 * Admin service for OGEMA widgets.
 */
public interface WidgetAdminService {
 
	/** 
	 * Set session expiry time in ms. <br>
	 * -1: use default value 
	 */ 
	void setSessionExpiryTime(WidgetPage<?> page, long sessionExpiryTime);
	
	/** 
	 * Set maximum nr. of parallel sessions. <br>
	 * -1: use default value
	 */
	void setMaxNrSessions(WidgetPage<?> page, int maxNrSessions);

	/**
	 * Get all registered widget apps.
	 * @return
	 */
	Map<String,WidgetApp> getRegisteredApps();
	
	/**
	 * Get a specific app
	 * @param baseUrl
	 * @return
	 * 		null if the app is not found, the app otherwise
	 */
	WidgetApp getApp(String baseUrl);
	
	/**
	 * Get all widgets for a given page, in the order they are loaded.
	 * This does not include session widgets which only exists for a specific user session.
	 * @param page
	 * @return
	 */
	List<OgemaWidget> getPageWidgets(WidgetPage<?> page);
	
	/**
	 * Returns the current number of sessions for the page.
	 * @param page
	 * @return
	 */
	int getNumberOfSessions(WidgetPage<?> page);
	
	/**
	 * Returns the current number of sessions for the app.
	 * @param page
	 * @return
	 */
	int getNumberOfSessions(WidgetApp app);
	
	/**
	 * Delete all user sessions. May be useful as a debugging tool.
	 */
	void deleteAllSessions();
	
	/**
	 * Normally the access count for a WidgetPage is stored in memory, but it
	 * can be written to a resource as well, so that it is stored persistently 
	 * across restarts. The target resource must be passed to this method.
	 * Note that the method must be called again on every restart.
	 * @param page
	 * @param count
	 */
	void setPersistentAccessCount(WidgetPage<?> page, IntegerResource count);
	
	/**
	 * Count page access for specific URL parameters only. This does not influence the
	 * generic counter.  
	 * @param page
	 * @param count
	 * @param parameters
	 * 		must not be empty.
	 */
	void setPersistentAccessCountForParameters(WidgetPage<?> page, IntegerResource count, Map<String,String[]> parameters);
	
	/**
	 * Returns the number of times the page has been accessed by the user.
	 * @param page
	 * @return
	 */
	int getAccessCount(WidgetPage<?> page);
	
}
