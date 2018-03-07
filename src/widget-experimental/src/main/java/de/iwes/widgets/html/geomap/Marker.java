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
import java.util.Objects;

import org.json.JSONObject;

public class Marker {

	// must be unique per widget/session
	private final String id;
	private final double[] position;
	final JSONObject json;
	private String iconUrl;
	private int[] size; 
	private String title;
	private String infoWindowHtml;
//	private Short infoWindowMaxSize; // in px // TODO
	
	public Marker(String id, double[] position) {
		this.id = Objects.requireNonNull(id).trim();
		if (this.id.isEmpty())
			throw new IllegalArgumentException("Illegal id... empty");
		this.position = Objects.requireNonNull(position);
		if (position.length != 2) 
			throw new IllegalArgumentException("Illegal position argument");
		this.json = getJSONInternal(id, position);
	}

	public String getId() {
		return id;
	}

	public double[] getPosition() {
		return position;
	}
	
	/**
	 * @param iconUrl
	 * @param size
	 * 		the icon size in px; may be null
	 */
	public void setIcon(String iconUrl, int[] size) {
		if (size != null) {
			if (size.length != 2 || size[0] <= 0 || size[1] <= 0)
				throw new IllegalArgumentException("Icon size must be an array of two positive integers, got " + Arrays.toString(size));
		}
		this.iconUrl = iconUrl;
		this.size = size;
		if (iconUrl == null) {
			json.remove("icon");
			json.remove("iconSize");
		}
		else {
			json.put("icon", iconUrl);
			if (size != null) 
				json.put("iconSize", size);
		}
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	/**
	 * The icon size, if set explicitly, or null otherwise.
	 * @return
	 */
	public int[] getIconSize() {
		return size == null ? null : size.clone();
	}
	
	public void setTitle(String title) {
		this.title = title;
		if (title == null)
			json.remove("title");
		else
			json.put("title", title);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setInfoWindowHtml(String html) {
		this.infoWindowHtml = html;
		if (html == null)
			json.remove("infoWindow");
		else
			json.put("infoWindow", html);
	}
	
	public String getInfoWindowHtml() {
		return infoWindowHtml;
	}
	
	private static final JSONObject getJSONInternal(final String id, final double[] position) {
		final JSONObject json = new JSONObject();
		final JSONObject latLng = new JSONObject();
		json.put("id",id);
		latLng.put("lat", position[0]);
		latLng.put("lng", position[1]);
		json.put("position", latLng);
		return json;
	}
	
	// XXX JSONObject is not cloneable... internally, use the json object directly, instead
	public JSONObject getJson() {
		return new JSONObject(json.toString());
	}
	
}
