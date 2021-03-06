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

import java.util.Map;
import java.util.function.Consumer;

import org.ogema.core.application.Application;

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
	 * Beta: proposed method. Lazy loading page, that will only be initialized once the page is accessed for the 
	 * first time. Note: the {@link Consumer#accept(Object)} method may be called multiple times.
	 * @param callback
	 */
	public <D extends LocaleDictionary> void createLazyStartPage(Consumer<WidgetPage<D>> callback);
	/**
	 * Beta: proposed method. Lazy loading page, that will only be initialized once the page is accessed for the 
	 * first time. Note: the {@link Consumer#accept(Object)} method may be called multiple times.
	 * @param relativUrl
	 * @param callback
	 * @param asStartPage
	 */
	public <D extends LocaleDictionary> void createLazyPage(String relativUrl, Consumer<WidgetPage<D>> callback, boolean asStartPage);
	
	/**
	 * Beta: proposed method. Lazy loading page, that will only be initialized once the page is accessed for the 
	 * first time. Note: the {@link Consumer#accept(Object)} method may be called multiple times.
	 * @param callback
	 * @param pageType
	 */
	public <D extends LocaleDictionary> void createLazyStartPage(Consumer<WidgetPage<D>> callback, @SuppressWarnings("rawtypes") Class<? extends WidgetPage> pageType);
	/**
	 * Beta: proposed method. Lazy loading page, that will only be initialized once the page is accessed for the 
	 * first time. Note: the {@link Consumer#accept(Object)} method may be called multiple times.
	 * @param relativUrl
	 * @param callback
	 * @param pageType
	 * @param asStartPage
	 */
	public <D extends LocaleDictionary> void createLazyPage(String relativUrl, Consumer<WidgetPage<D>> callback, @SuppressWarnings("rawtypes") Class<? extends WidgetPage> pageType, boolean asStartPage);
	
	/**
	 * Register a resource with the WidgetApp.
	 * @param path
	 * 		File name of the resource within the `resources` directory.
	 * @param app
	 * 		May be null, in which case no check for the file's existence is run,
	 * 		resulting in 404s if the file at `path` doesn't exist.
	 * @return
	 * 		URL to the resource or null if it couldn't be registered.
	 */
	public default String addResource(String stylesheetName, Class<? extends Application> app) { return null; }

	/**
	 * Add a CSS stylesheet to all pages of the WidgetApp.
	 * @param stylesheetName
	 * 		File name of the stylesheet within the `resources` directory.
	 * @param app
	 * 		May be null, in which case no check for the file's existence is run,
	 * 		resulting in 404s if the `styleSheet` file doesn't exist.
	 * @return
	 * 		True iff the stylesheet was successfully applied.
	 */
	public default boolean addStylesheet(String stylesheetName, Class<? extends Application> app) { return false; }
	
	/**
	 * Get the pages registered for this widget app. 
	 * @return
	 * 		Map&lt;URL relativ to app base path, page&gt;
	 */
	public Map<String,WidgetPage<?>> getPages();

}

