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
package de.iwes.widgets.html.geomap;

import de.iwes.widgets.template.DisplayTemplate;

public interface MapTemplate<T> extends DisplayTemplate<T> {

	double getLatitude(T instance);
	/**
	 * In degrees (-180� - +180�)
	 * @param instance
	 * @return
	 */
	double getLongitude(T instance);
	String getIconUrl(T instance);
	/**
	 * Icon size in pixels (two-element array). May return null.
	 * @param instance
	 * @return
	 */
	int[] getIconSize(T instance);

}
