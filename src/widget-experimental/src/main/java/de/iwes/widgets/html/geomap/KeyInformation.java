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

import java.util.Objects;

import de.iwes.widgets.html.geomap.MapProvider.DisplayStyle;

class KeyInformation {
	
	// never null
	private final MapProvider mapType;
	// may be null, in which case the key applies to all styles of the map type
	private final DisplayStyle displayStyle;
	private final String key;
	
	public KeyInformation(MapProvider mapType, DisplayStyle displayStyle, String key) {
		this.mapType = Objects.requireNonNull(mapType);
		this.displayStyle = displayStyle;
		this.key = Objects.requireNonNull(key);
	}
	
	public String getKey() {
		return key;
	}
	
	public boolean applies(final MapProvider type, final DisplayStyle style) {
		if (type != mapType)
			return false;
		return (displayStyle == null || style == displayStyle);
	}
	
	
	
}
