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
