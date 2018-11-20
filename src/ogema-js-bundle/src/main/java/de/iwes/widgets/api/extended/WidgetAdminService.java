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
	 * @param app
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
