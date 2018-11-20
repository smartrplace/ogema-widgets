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
package de.iwes.widgets.api.extended.impl;

import java.util.List;

import javax.servlet.Servlet;

import org.ogema.core.security.WebAccessManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetComparator;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Internal service interface for widgets
 * @author esternberg
 */
public interface OgemaOsgiWidgetService extends Servlet {
    /** Register widget on the page
     * @param boundPagePath servletPath without Host and .htm(l)*/
	public ConfiguredWidget<?> registerWidgetNew(OgemaWidgetBase<?> widget, String boundPagePath, WebAccessManager wam);  
	/** register a session-specific widget **/
    public ConfiguredWidget<?> registerWidgetNew(OgemaWidgetBase<?> widget, String boundAppPath,WebAccessManager wam, OgemaHttpRequest request); // TODO unregister method
    // FIXME
    /** Set widget to be the init widget of the page. Only call this once per page
     */
//	public void setTnitWidget(OgemaWidgetBase<?> widget, String boundPagePath, boolean initStatus);
	// FIXM
	/** The getWidgetInformation of an independent widget is called by the central servlet during its main loop.
	 * Dependent widgets should be called by another independent widget or by the init widget of the page.
	 * */
//	public void setWidgetDepedencyStatus(OgemaWidgetBase<?> widget, String boundPagePath, boolean dependencyStatus);
    
    public void unregisterWidget(String boundPagePath, OgemaWidgetBase<?> widget);
//	public void registerApp(WidgetApp widgetApp);
    
    /**
     * Induces the correct ordering of widgets on the page, assuming that the page widgets have already been ordered by means of the 
     * {@link WidgetComparator}. 
     * Note that this only sorts non-session-specific widgets at the moment.
     * Session-specific widgets are sorted behind global widgets, though, so that it should be safe to use the comparator on a set of
     * widgets which includes session-specific ones.<br>
     * Not thread-safe.
     * @param widgets
     * 		all widgets must belong to the same page
     */
    public void sortWidgets(List<OgemaWidgetBase<?>> widgets);
	
	
}
