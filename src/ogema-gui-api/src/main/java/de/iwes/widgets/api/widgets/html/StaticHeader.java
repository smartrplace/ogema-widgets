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

/**
 * This is a static header, i.e. it does not provide localisation options.
 * Use a widget instead for more flexible headers. See for instance
 * org.ogema.tools.widget.html.form.label.Header.
 */
public class StaticHeader extends HtmlItem {
	
	/**
	 * Default size header (corresponds to &lt;h1&gt; tag)
	 * @param headerText
	 */
	public StaticHeader(String headerText) {
		this(1, headerText);
	}
	
	/**
	 * @param size from <br>
	 * 		1: largest <br>
	 * 		6: smallest 	
	 * @param headerText
	 */
	public StaticHeader(int size, String headerText) {
		super("h" + size);
		addSubItem(headerText);
	}
	
	
}
