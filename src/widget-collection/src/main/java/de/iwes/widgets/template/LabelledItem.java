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
