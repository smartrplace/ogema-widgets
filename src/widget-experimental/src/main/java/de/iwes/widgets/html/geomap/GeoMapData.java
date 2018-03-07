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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.geomap.MapProvider.DisplayStyle;

public class GeoMapData extends WidgetData {

	private String height;
	private double[] center;
	private short zoom;
	protected final Map<String, Marker> markers = new HashMap<>();
	private MapLib lib;
	private MapProvider provider; // e.g. google or openstreetmap
	private DisplayStyle displayType; // e.g. Roadview or Satellite
	private final boolean trackMarkers;
	protected String selectedMarker;
	
	protected GeoMapData(GeoMap widget, boolean trackMarkers) {
		super(widget);
		this.trackMarkers = trackMarkers;
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		// TODO recycle?
		final JSONObject obj = new JSONObject();
		final JSONObject options = new JSONObject();
		final JSONObject latLng = new JSONObject();
		final String apiKey = ((GeoMap) widget).getKey(provider);
		final OgemaWidgetBase<?> infoWidget = ((GeoMap) widget).infoWindowWidget;
		readLock();
		try {
			if (apiKey != null)
				obj.put("apiKey", apiKey);
			if (height != null)
				obj.put("height", height);
			latLng.put("lat", center != null ? center[0] : 51.313645);
			latLng.put("lng", center != null ? center[1] : 9.447138);
			options.put("center", latLng);
			options.put("zoom", zoom);
			obj.put("options", options);
			obj.put("trackMarkers", trackMarkers);
			obj.put("markers", serialize(this.markers));
//			obj.put("type", type.getId());
//			obj.put("displayStyle",displayType);
			obj.put("lib", lib.getJson());
			obj.put("type", provider.getJson(displayType));
			if (infoWidget != null) {
				final JSONObject infoJson = new JSONObject();
				infoJson.put("id", infoWidget.getId());
				infoJson.put("tag", infoWidget.getTag());
				obj.put("infoWidget", infoJson);
			}
		} finally {
			readUnlock();
		}
		return obj;
	}
	
	// TODO issued when a new marker is selected
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		final JSONObject json = new JSONObject(data).getJSONObject("data");
		// doesn't really make sense in a global widget, so we could probably skip the lock here
		writeLock();
		try {
			selectedMarker = json.has("marker") ? json.getString("marker") : null;
		} finally {
			writeUnlock();
		}
		return json;
	}
	
	private final static JSONObject serialize(final Map<String,Marker> markers) {
		final JSONObject o = new JSONObject();
		for (Map.Entry<String, Marker> entry : markers.entrySet()) {
			o.put(entry.getKey(), entry.getValue().json);
		}
		return o;
	}

//	protected String getApiKey() {
//		readLock();
//		try {
//			return apiKey;
//		} finally {
//			readUnlock();
//		}
//	}
//
//	protected void setApiKey(String apiKey) {
//		writeLock();
//		try {
//			this.apiKey = apiKey;
//		} finally {
//			writeUnlock();
//		}
//	}

	protected String getHeight() {
		readLock();
		try {
			return height;
		} finally {
			readUnlock();
		}
	}

	protected void setHeight(String height) {
		writeLock();
		try {
			this.height = height;
		} finally {
			writeUnlock();
		}
	}

	protected double[] getCenter() {
		readLock();
		try {
			return center;
		} finally {
			readUnlock();
		}
	}

	protected void setCenter(double[] center) {
		Objects.requireNonNull(center);
		if (center.length != 2)
			throw new IllegalArgumentException("Center coordinates must be an array of length 2, got " + Arrays.toString(center));
		writeLock();
		try {
			this.center = center;
		} finally {
			writeUnlock();
		}
	}

	protected short getZoom() {
		readLock();
		try {
			return zoom;
		} finally {
			readUnlock();
		}
	}

	protected void setZoom(short zoom) {
		if (zoom < 0)
			throw new IllegalArgumentException("Zoom factor must be non-negative, got " + zoom);
		writeLock();
		try {
			this.zoom = zoom;
		} finally {
			writeUnlock();
		}
	}
	
	protected Marker addMarker(Marker marker) {
		writeLock();
		try {
			return markers.put(marker.getId(), marker);
		} finally {
			writeUnlock();
		}
	}
	
	protected Marker removeMarker(Marker marker) {
		writeLock();
		try {
			return markers.remove(marker.getId());
		} finally {
			writeUnlock();
		}
	}
	
	protected void addMarkers(Collection<Marker> markers) {
		writeLock();
		try {
			for (Marker m : markers) {
				this.markers.put(m.getId(), m);
			}
		} finally {
			writeUnlock();
		}
	}
	
	protected void clearMarkers() {
		writeLock();
		try {
			this.markers.clear();
		} finally {
			writeUnlock();
		}
	}

	protected MapProvider getMapProvider() {
		readLock();
		try {
			return provider;
		} finally {
			readUnlock();
		}
	}

	protected void setType(MapLib lib, MapProvider provider, DisplayStyle displayStyle) {
		Objects.requireNonNull(lib);
		Objects.requireNonNull(provider);
		Objects.requireNonNull(displayStyle);
		if (!lib.getSupportedMaps().contains(provider))
			throw new IllegalArgumentException("Unsupported map type " + provider + " for library " + lib);
		if (!provider.supportsType(displayStyle)) {
			throw new IllegalArgumentException("Unsupported display style " + displayStyle.id() + " for map type " + provider);
		}
		writeLock();
		try {
			this.lib = lib;
			this.provider = provider;
			this.displayType = displayStyle;
		} finally {
			writeUnlock();
		}
	}
	
	protected MapLib getLib() {
		readLock();
		try {
			return lib;
		} finally {
			readUnlock();
		}
	}

	protected Marker getSelectedMarker() {
		readLock();
		try {		
			return selectedMarker != null ? markers.get(selectedMarker) : null;
		} finally {
			readUnlock();
		}
	}
	
}
