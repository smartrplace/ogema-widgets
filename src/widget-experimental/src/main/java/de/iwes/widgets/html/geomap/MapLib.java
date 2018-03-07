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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

public enum MapLib {
	
	GOOGLE("GoogleMap", Collections.singletonList(MapProvider.GOOGLE)), 
	OPENLAYERS("OpenLayersMap",Arrays.asList(MapProvider.OPENSTREETMAP, MapProvider.OPENTOPOMAP, MapProvider.BING));

	private final List<MapProvider> maps;
	private final String jsClassName;
	private final JSONObject json;
	
	private MapLib(String jsClassName, List<MapProvider> maps) {
		this.maps=maps;
		this.jsClassName = jsClassName;
		this.json = new JSONObject();
		json.put("id", name().toLowerCase());
		json.put("jsClassName", jsClassName);
	}
	
	public List<MapProvider> getSupportedMaps() {
		return maps;
	}
	
	public String getJsClassName() {
		return jsClassName;
	}
	
	JSONObject getJson() {
		return json;
	}
	
}
