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

public class MarkerType {

	public final String id;
	public final double lat;
	public final double lng;
	public final String title;
	public final String iconUrl;
	
	public MarkerType(String id, double lat, double lng, String title, String iconUrl) {
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.title = title;
		this.iconUrl = iconUrl;
	}
	
}
