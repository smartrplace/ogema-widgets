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
package de.iwes.widgets.test3.template.impl;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.geomap.MapTemplate;

public class MarkerTemplate implements MapTemplate<MarkerType> {

	@Override
	public String getId(MarkerType object) {
		return object.id;
	}

	@Override
	public String getLabel(MarkerType object, OgemaLocale locale) {
		return object.title;
	}

	@Override
	public double getLatitude(MarkerType instance) {
		return instance.lat;
	}

	@Override
	public double getLongitude(MarkerType instance) {
		return instance.lng;
	}

	@Override
	public String getIconUrl(MarkerType instance) {
		return instance.iconUrl;
	}
	
	@Override
	public int[] getIconSize(MarkerType instance) {
		return new int[]{50,50}; 
	}

}
