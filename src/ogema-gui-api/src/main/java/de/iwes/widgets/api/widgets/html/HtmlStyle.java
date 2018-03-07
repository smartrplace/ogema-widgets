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

package de.iwes.widgets.api.widgets.html;

import java.util.LinkedHashMap;
import java.util.Map;

/** 
 * Defines the style of a {@link HtmlItem}. Use 
 * {@link HtmlItem#addStyle(HtmlStyle)} to set the style of 
 * an HtmlItem.
 */
public class HtmlStyle {
	
	public static final HtmlStyle BOLD_TEXT = new HtmlStyle("style", "font-weight:bold;");
	public static final HtmlStyle ALIGNED_CENTER = new HtmlStyle("class", "text-center");
	public static final HtmlStyle ALIGNED_LEFT = new HtmlStyle("class", "text-left");
	public static final HtmlStyle ALIGNED_RIGHT = new HtmlStyle("class", "text-right");
	
	private final Map<String,String> attr;
	
	public HtmlStyle(Map<String,String> attr) {
		this.attr = attr;
	}
	
	public HtmlStyle(String attrName,String attrValue) {
		this.attr = new LinkedHashMap<String, String>();
		attr.put(attrName, attrValue);
	}
	
	public Map<String,String> getAttributes() {
		return new  LinkedHashMap<String,String>(attr);
	}
}
