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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration of the menu for {@link de.iwes.widgets.api.widgets.WidgetPage}. Allows to modify the icon shown (default: OGEMA logo),
 * to toggle visibility of the language selection and navigation menus, and to add a custom navigation menu.
 *
 */
public class MenuConfiguration {
	public final static boolean HIDE_NAVI  = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
		
		@Override
		public Boolean run() {
			return Boolean.getBoolean("org.ogema.widgets.hideNavi");
		}
		
	});
	public final static boolean HIDE_MESSAGES  = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
		
		@Override
		public Boolean run() {
			return Boolean.getBoolean("org.ogema.widgets.hideMessages");
		}
		
	});
	
	// default values
	private boolean menuVisible = true;
	private boolean navigationVisible = !HIDE_NAVI;
	private boolean languageSelectionVisible = true;
	private boolean showLogoutBtn = true;
	private boolean showMessages = !HIDE_MESSAGES;
	private String iconPath = "/ogema/icon";
	private final Set<NavbarType> type = new HashSet<NavbarType>();
	private NavigationMenu customNavigation = null;
	
	/**** Getters and setters ****/
	public boolean isMenuVisible() {
		return menuVisible;
	}

	public void setMenuVisible(boolean menuVisible) {
		this.menuVisible = menuVisible;
	}

	public boolean isNavigationVisible() {
		return navigationVisible;
	}

	/**
	 * Hide or show apps navigation. Default: true (visible)
	 * @param navigationVisible
	 */
	public void setNavigationVisible(boolean navigationVisible) {
		this.navigationVisible = navigationVisible;
	}

	public boolean isLanguageSelectionVisible() {
		return languageSelectionVisible;
	}

	public void setLanguageSelectionVisible(boolean languageSelectionVisible) {
		this.languageSelectionVisible = languageSelectionVisible;
	}
	
	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}


	public NavigationMenu getCustomNavigation() {
		return customNavigation;
	}

	/**
	 * use to add custom dropdown menu to menu bar
	 */
	public void setCustomNavigation(NavigationMenu customNavigation) {
		this.customNavigation = customNavigation;
	}
	
	public boolean isShowLogoutBtn() {
		return showLogoutBtn;
	}

	public void setShowLogoutBtn(boolean showLogoutBtn) {
		this.showLogoutBtn = showLogoutBtn;
	}

	public Set<NavbarType> getTypes() {
		return type;
	}

	public void setType(NavbarType type) {
		this.type.clear();
		this.type.add(type);
	}
	
	public void addType(NavbarType type) {
		this.type.add(type);
	}

	public boolean isShowMessages() {
		return showMessages;
	}

	public void setShowMessages(boolean showMessages) {
		this.showMessages = showMessages;
	}

	
}
