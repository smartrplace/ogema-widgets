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