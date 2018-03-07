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

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration of the menu for {@link WidgetPageSimple}. Allows to modify the icon shown (default: OGEMA logo),
 * to toggle visibility of the language selection and navigation menus, and to add a custom navigation menu.
 *
 */
public class MenuConfiguration {
	
	// default values
	private boolean menuVisible = true;
	private boolean navigationVisible = true;
	private boolean languageSelectionVisible = true;
	private boolean showLogoutBtn = true;
	private boolean showMessages = true;
	private String iconPath = "/ogema/img/svg/ogema.svg";
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
