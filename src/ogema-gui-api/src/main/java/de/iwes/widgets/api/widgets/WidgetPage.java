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

import java.util.Collection;
import java.util.Map;

import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A user page.
 */
public interface WidgetPage<D extends LocaleDictionary> {
	
	/**
	 * The widget app this page belongs to.
	 * @return
	 */
	public WidgetApp getWidgetApp();
	
	/**
	 * @return The page URL relative to the app base URL.
	 * 		See {@link WidgetApp#appUrl()}
	 */
	public String getUrl();
	
	/**
	 * @return The full URL of the page.
	 */
	public String getFullUrl();
	
	/**
	 * Create a new widget group. Widget groups can perform joint Http requests, if they are 
	 * triggered by another widget through 
	 * {@link OgemaWidget#triggerAction(WidgetGroup, org.ogema.gui.api.widgets.dynamics.TriggeringAction, 
	 *  org.ogema.gui.api.widgets.dynamics.TriggeredAction) OgemaWidgetI#triggerGroupAction}, 
	 *  or one of the related methods. Think of forms with multiple input fields, and a triggering button that
	 *  causes the data from all form fields to be sent to the server.
	 * @param groupId
	 * 		a unique (per page) groud id
	 * @param widgets
	 * 		initial set of widgets belonging to this group
	 * @return
	 */
	public WidgetGroup registerWidgetGroup(String groupId, Collection<OgemaWidget> widgets);
	
	/**
	 * @see #registerWidgetGroup(String, Collection)
	 * Initially, the created group does not contain any widgets. Use {@link WidgetGroup#addWidget(OgemaWidget)}
	 * to add widgets.
	 * 
	 * @param groupId
	 * @return
	 */
	public WidgetGroup registerWidgetGroup(String groupId);
	
	/**
	 * Note: this does not destroy the widgets inside the group
	 * @param group
	 */
	public void removeWidgetGroup(WidgetGroup group);
	
	/**
	 * Set page title
	 * @param title
	 * @return	this
	 */
	public WidgetPage<D> setTitle(String title);
	
	/**
	 * Append a widget to the page. Must be called explicitly for all widgets
	 * to be shown on the page.
	 * @param widget
	 * @return
	 * 		this
	 */
	public WidgetPage<?> append(OgemaWidget widget);
	
	/**
	 * Append some html to the page. 
	 * @param htmlItem
	 * @return
	 * 		this
	 */
	public WidgetPage<?> append(HtmlItem htmlItem);
	
	/**
	 * Append some text to the page.
	 * @param text
	 * @return
	 * 		this
	 */
	public WidgetPage<?> append(String text);
	
	/**
	 * Append a line break to the page.
	 * 
	 * @return
	 * 		this
	 */
	public WidgetPage<D> linebreak();
	
	/**
	 * Register a localisation that implements the dictionary type for
	 * a specific language. 
	 * @see LocaleDictionary
	 * 
	 * @param clazz an implementation of the dictionary type
	 * @return
	 * 		this
	 */
	public <T extends D> WidgetPage<D> registerLocalisation(Class<T> clazz);
	
	/**
	 * Returns the page dictionary for the language specified by some request.
	 * If a localisation for the requested language has not been registered via
	 * {@link #registerLocalisation(Class)} yet, the default language for the JVM
	 * is used. If no localisation for this language is available, English is used. 
	 * If no English language version of the dictionary is available, the first registered
	 * language is used. 
	 * @param req
	 * @return
	 */
	public D getDictionary(OgemaHttpRequest req);
	
	/**
	 * Returns the page dictionary for the specified language identifier
	 * (according to ... TODO). 
	 * @see #getDictionary(OgemaHttpRequest) 
	 * 
	 * @param language
	 * @return
	 */
	public D getDictionary(String language);
	
	/**
	 * Call this once before the page is loaded, to display a spinner at a widget's location while
	 * it is loading (has a pending GET or POST request).
	 * @param show
	 * 		true: show spinner; false: do not show (default)
	 */
	public void showOverlay(boolean show);
	
	/**
	 * By default, widget pages display a menu at the top of the screen. Its settings can be 
	 * changed via the menu configuration, which also allows to hide the menu completely.
	 * Furthermore, it is possible, to display a custom navigation or to hide certain submenus. 
	 */
	public MenuConfiguration getMenuConfiguration();
	
	/**
	 * @param backgroundImg
	 * 		The link to a background image. For instance, the provided sample images can be used,
	 * 		org.ogema.tools.widget.api.html.bricks.SampleImages:<br>
	 * 		<code>widgetPage.setBackgroundImg(SampleImages.BLUE_BLEND_LIGHTS);</code>
	 */
	public void setBackgroundImg(String backgroundImg);
	
	/**
	 * Get the request parameters for a particular session, i.e. the key-value pairs
	 * in the page url, of the form
	 * <code>?key1=value1&amp;key2=value2a,value2b&amp;key3=...</code>
	 * @return
	 */
	public Map<String,String[]> getPageParameters(OgemaHttpRequest req);
	
	/**
	 * Get a widget group that represents all widgets on the page. It does not support
	 * adding or removing of widgets, and neither the setting of a polling interval.
	 * 
	 * It does not include session-specific widgets either. 
	 * @return
	 */
	// XXX session widgets?
	public WidgetGroup getAllWidgets();
	
	public OgemaWidget getTriggeringWidget(OgemaHttpRequest req);
	
}

