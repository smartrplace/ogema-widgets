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
package de.iwes.widgets.template;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public interface LabelledItem {
	
	/**
	 * Unique id at least amongst all objects of the same class / interface implementing LabelledItem
	 * @return
	 */
	String id();
	
	/**
	 * Human readable short label. 
	 * If the passed locale is not supported, a default value shall be returned.
	 * @param locale
	 * @return
	 */
	String label(OgemaLocale locale);
	
	/**
	 * More extensive human readable description that might only be shown in a tool-tip or similar.
	 * If the passed locale is not supported, a default value shall be returned.
	 * Unless stated otherwise, null is allowed as a return value, and means that no tooltip/information is shown.
	 * @param locale
	 * @return
	 */
	default String description(OgemaLocale locale) {
		return null;
	};

}
