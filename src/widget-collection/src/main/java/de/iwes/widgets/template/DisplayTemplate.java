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

public interface DisplayTemplate<T> {

	/**
	 * Must return a unique id
	 * @param object
	 * @return
	 */
	String getId(T object);
	
	/**
	 * The label to be displayed on the page.
	 * @param object
	 * @return
	 */
	String getLabel(T object, OgemaLocale locale);
	
	/**
	 * A description, e.g. for display in a tooltip. This may return null;
	 * @param object
	 * @param locale
	 * @return
	 */
	default String getDescription(T object, OgemaLocale locale) {
		if (object instanceof LabelledItem)
			return ((LabelledItem) object).description(locale);
		return null;
	}
	
	/**
	 * Relevant when opt groups are active
	 * @param object
	 * @param locale
	 * @return
	 */
	default String getOptGroup(T object, OgemaLocale locale) {
		return null;
	}
	
}
