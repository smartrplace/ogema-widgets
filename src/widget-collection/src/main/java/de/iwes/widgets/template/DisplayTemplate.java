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
	
}
