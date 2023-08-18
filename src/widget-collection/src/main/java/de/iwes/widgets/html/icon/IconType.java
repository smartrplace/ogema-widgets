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
	public static final IconType EXTERNAL_LINK = new IconType(BASE_PATH + "/publicicons/link.svg");

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
