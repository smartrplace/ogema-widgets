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
package de.iwes.widgets.api.widgets.navigation;

import java.util.LinkedHashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.WidgetPage;

/**
 * use to add custom dropdown menu to menu bar; see {@link MenuConfiguration#setCustomNavigation(NavigationMenu)}.
 */
public class NavigationMenu {
	
	private final String title;
	private String glyphiconClass = "glyphicon glyphicon-th-list";
	private final Map<String,String> navMap = new LinkedHashMap<String, String>();
	
	public NavigationMenu(String title) {
		this.title = title;
	}
	
	/**
	 * Consider using {@link #addEntry(String, WidgetPage)} instead.
	 * @param text
	 * @param link
	 */
	public void addEntry(String text,String link) {
		navMap.put(text, link);
	}
	
	public void addEntry(String text,WidgetPage<?> page) {
		addEntry(text, page.getFullUrl());
	}
	
	public Map<String,String> getEntries() {
		return navMap;
	}
	
	/**
	 * Set icon to be displayed. See http://getbootstrap.com/components/ for options.
	 * Examples:
	 * <ul>
	 * 	<li>"glyphicon glyphicon-plus"</li>
	 * 	<li>"glyphicon glyphicon-music"</li>
	 * </ul>
	 * Default is "glyphicon glyphicon-th-list". Set to null or empty string to remove icon.
	 */
	public void setGlyphiconType(String glyphiconType) {
		glyphiconClass = glyphiconType;
	}
	
	public String getGlyphicon() {
		return glyphiconClass;
	}
	
	public String getTitle() {
		return title;
	}
	
}