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
 * Add a line break to an Html page. 
 */
public class Linebreak extends HtmlItem {

	private volatile static Linebreak instance;

	public Linebreak() {
		super("br", null);
	}

	/**
	 * Returns a single global linebreak instance. If it is necessary to selectively
	 * remove individual line breaks it is better to construct new instances via the constructor
	 * @return
	 */
	public static Linebreak getInstance() {
		if (instance == null) {
			instance = new Linebreak();
		}
		return instance;
	}
	
	
}
