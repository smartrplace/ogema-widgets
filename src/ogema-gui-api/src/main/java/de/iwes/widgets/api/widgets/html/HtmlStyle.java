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
