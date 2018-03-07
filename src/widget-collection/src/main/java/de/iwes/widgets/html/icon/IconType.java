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

package de.iwes.widgets.html.icon;

/**
 * Think of an enum. But unlike an enum, this class can be extended with custom icon types.
 *
 */
public class IconType {

	private static final String BASE_PATH = "/ogema/widget/icon/icons";

	public static final IconType CHECK_MARK = new IconType(BASE_PATH + "/tango/Dialog-accept.svg");
	public static final IconType FAIL = new IconType(BASE_PATH + "/tango/Dialog-error-round.svg");
	public static final IconType REFRESH = new IconType(BASE_PATH + "/tango/View-refresh.svg");
	public static final IconType FORBIDDEN = new IconType(BASE_PATH + "/tango/Edit-delete.svg");
	public static final IconType SETTINGS1 = new IconType(BASE_PATH + "/tango/Applications-system.svg");
	public static final IconType SETTINGS2 = new IconType(BASE_PATH + "/tango/Preferences-system.svg");
	public static final IconType CLOSE = new IconType(BASE_PATH + "/tango/Emblem-unreadable.svg");
	public static final IconType IMPORTANT = new IconType(BASE_PATH + "/tango/Emblem-important.svg");
	public static final IconType HELP_CONTENT = new IconType(BASE_PATH + "/tango/Help-content.svg");

	private final String browserPath;

	public IconType(String browserPath) {
		this.browserPath = browserPath;
	}

	public String getBrowserPath() {
		return browserPath;
	}

	public static IconType[] values() {
		return new IconType[] {CHECK_MARK, FAIL, REFRESH, FORBIDDEN, SETTINGS1, SETTINGS2, CLOSE, IMPORTANT, HELP_CONTENT};
	}

	@Override
	public String toString() {
		if (this == CHECK_MARK)
			return "CHECK_MARK";
		if (this == FAIL)
			return "FAIL";
		if (this == REFRESH)
			return "REFRESH";
		if (this == FORBIDDEN)
			return "FORBIDDEN";
		if (this == SETTINGS1)
			return "SETTINGS1";
		if (this == SETTINGS2)
			return "SETTINGS2";
		if (this == CLOSE)
			return "CLOSE";
		if (this == IMPORTANT)
			return "IMPORTANT";
		if (this == HELP_CONTENT)
			return "HELP_CONTENT";
		return getBrowserPath();
	}

}
