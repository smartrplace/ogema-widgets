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

public class DefaultDisplayTemplate<T> implements DisplayTemplate<T> {
	
	@Override
	public String getLabel(T object, OgemaLocale locale) {
		if (object instanceof LabelledItem) {
			final String label = ((LabelledItem) object).label(locale);
			if (label != null) // should not return null, but we never know...
				return label;
		}
		return object.toString();
	}
	
	@Override
	public String getId(T object) {
		return object instanceof LabelledItem ? ((LabelledItem) object).id() : object.toString();
	}

}
