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

package de.iwes.widgets.api.widgets;

import java.util.Map;

import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

/**
 * A widget app consists of one or multiple Widget pages,
 * which share a common URL prefix.
 */
public interface WidgetApp {

	/**
	 * Returns the common URL prefix for this Widget app.
	 * @return
	 */
	public String appUrl();
	
	/**
	 * Unregisters all pages from the Http service.
	 */
	public void close();
	
	/**
	 * Create a new page and generate the corresponding HTML file. The url of 
	 * the page relative to the common prefix of the app is "index.html", and the
	 * page will be registered as the app's start page.
	 * @param <D>
	 * @return
	 */
	public <D extends LocaleDictionary> WidgetPage<D> createStartPage();
	
	/**
	 * Create a new page and generate the corresponding HTML file. The page is not registered
	 * as the app's start page.
	 * @param relativeUrl
	 * 		url relative to the common url prefix of the app. Should end in ".html", otherwise
	 * 		it is appended.
	 * @param <D>
	 * @return
	 */
	public <D extends LocaleDictionary> WidgetPage<D> createWidgetPage(String relativeUrl);
	
	/**
	 * Create a new page and generate the corresponding HTML file.
	 * @param relativeUrl
	 * 		url relative to the common url prefix of the app. Should end in ".html", otherwise
	 * 		it is appended.
	 * @param setAsStartPage
	 * 		register this page as the app's start page at the OGEMA web service?
	 * @param <D>
	 * @return
	 */
	public <D extends LocaleDictionary> WidgetPage<D> createWidgetPage(String relativeUrl, boolean setAsStartPage);
	
	/**
	 * Get the pages registered for this widget app. 
	 * @return
	 * 		Map&lt;URL relativ to app base path, page&gt;
	 */
	public Map<String,WidgetPage<?>> getPages();

}

