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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.geomap.MapProvider.DisplayStyle;

public class GeoMap extends OgemaWidgetBase<GeoMapData> {

	private static final long serialVersionUID = 1L;
	private final Map<MapProvider, String> keys= new EnumMap<>(MapProvider.class);
 	private String defaultHeight;
	private double[] defaultCenter = {51.313645, 9.447138};
	private short defaultZoom = 0; 
	private boolean defaultTrackMarkers = true;
	private List<Marker> defaultMarkers;
	private MapLib defaultLib = MapLib.OPENLAYERS;
	private MapProvider defaultType = MapProvider.OPENSTREETMAP;
	private DisplayStyle defaultDisplayStyle = MapProvider.OPENSTREETMAP.getSupportedDisplayTypes().get(0);
	volatile OgemaWidgetBase<?> infoWindowWidget = null;

	public GeoMap(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public GeoMap(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		if (globalWidget)
			defaultTrackMarkers = false;
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return GeoMap.class;
	}

	@Override
	public GeoMapData createNewSession() {
		return new GeoMapData(this, defaultTrackMarkers);
	}
	
	@Override
	protected void setDefaultValues(GeoMapData opt) {
		opt.setHeight(defaultHeight);
		opt.setCenter(defaultCenter);
		opt.setZoom(defaultZoom);
		opt.setType(defaultLib, defaultType, defaultDisplayStyle);
		if (defaultMarkers != null)
			opt.addMarkers(defaultMarkers);
	}
	
	/**
	 * 
	 * @param mapType
	 * @param displayStyle
	 * 		may be null, in which case hte key applies to all display styles of the map type
	 * @param apiKey
	 */
	public void setApiKey(MapProvider mapType, String apiKey) {
		keys.put(mapType, apiKey);
	}
	
	protected String getKey(MapProvider mapType) {
		return keys.get(mapType);
	}
	
	public String getHeight(OgemaHttpRequest req) {
		return getData(req).getHeight();
	}

	/**
	 * @param height
	 * 		Can be either an explicit height (like "100%", "500px"), or null,
	 *      in which case the height is set to full window size 
	 *      -> this implies that no other widgets will be visible -> ?
	 * @param req
	 */
	public void setHeight(String height, OgemaHttpRequest req) {
		getData(req).setHeight(height);
	}
	
	/**
	 * @param height
	 * 		Can be either an explicit height (like "100%", "500px"), or null,
	 *      in which case the height is set to full window size 
	 *      -> this implies that no other widgets will be visible -> ?
	 * @param req
	 */
	public void setDefaultHeight(String height) {
		this.defaultHeight = height;
	}
	
	public double[] getCenter(OgemaHttpRequest req) {
		return getData(req).getCenter();
	}

	public void setCenter(double[] center, OgemaHttpRequest req) {
		Objects.requireNonNull(center);
		if (center.length != 2)
			throw new IllegalArgumentException("Center coordinates must be an array of length 2, got " + Arrays.toString(center));
		getData(req).setCenter(center);
	}
	
	/**
	 * @param center
	 * 		must be an array of length 2
	 */
	public void setDefaultCenter(double[] center) {
		Objects.requireNonNull(center);
		if (center.length != 2)
			throw new IllegalArgumentException("Center coordinates must be an array of length 2, got " + Arrays.toString(center));
		this.defaultCenter = center;
	}

	public short getZoom(OgemaHttpRequest req) {
		return getData(req).getZoom();
	}

	/**
	 * @param zoom
	 * 		a non-negative number, typically in the range 0 (zoomed out) - 20 (zoomed in) 
	 * @param req
	 */
	public void setZoom(short zoom, OgemaHttpRequest req) {
		if (zoom <= 0)
			throw new IllegalArgumentException("Zoom factor must be positive, got " + zoom);
		getData(req).setZoom(zoom);
	}
	
	/**
	 * @param zoom
	 * 		a non-negative number, typically in the range 0 (zoomed out) - 20 (zoomed in) 
	 */
	public void setDefaultZoom(short zoom) {
		if (zoom < 0)
			throw new IllegalArgumentException("Zoom factor must be non-negative, got " + zoom);
		this.defaultZoom = zoom;
	}
	
	public void setDefaultMarkers(Collection<Marker> markers) {
		this.defaultMarkers = new ArrayList<>(markers);
	}
	
	public Marker addMarker(Marker marker, OgemaHttpRequest req) {
		return getData(req).addMarker(marker);
	}
	
	public Marker removeMarker(Marker marker, OgemaHttpRequest req) {
		return getData(req).removeMarker(marker);
	}
	
	public void addMarkers(Collection<Marker> markers, OgemaHttpRequest req) {
		getData(req).addMarkers(markers);
	}
	
	public void clearMarkers(OgemaHttpRequest req) {
		getData(req).clearMarkers();
	}
	
	public MapProvider getMapProvider(OgemaHttpRequest req) {
		return getData(req).getMapProvider();
	}
	
	protected MapLib getMapLib(OgemaHttpRequest req) {
		return getData(req).getLib();
	}

	/**
	 * @param lib
	 * @param type
	 * @param displayStyle
	 * 		One of the supported display types of type. See {@link MapProvider#getSupportedDisplayTypes()}.
	 * @param req
	 * @throws IllegalArgumentException
	 * 		If displayStyle is not compatible with type, or provider is not compatible with lib
	 */
	public void setType(MapLib lib, MapProvider provider, DisplayStyle displayStyle, OgemaHttpRequest req) {
		getData(req).setType(lib, provider, displayStyle);
	}
	
	/**
	 * @param type
	 * @param displayStyle
	 * 		One of the supported display types of type. See {@link MapProvider#getSupportedDisplayTypes()}.
	 * @throws IllegalArgumentException
	 * 		If displayStyle is not compatible with type.
	 */
	public void setDefaultType(MapLib lib, MapProvider provider, DisplayStyle displayStyle) {
		Objects.requireNonNull(provider);
		Objects.requireNonNull(displayStyle);
		if (!lib.getSupportedMaps().contains(provider))
			throw new IllegalArgumentException();
		if (!provider.supportsType(displayStyle))
			throw new IllegalArgumentException();
		this.defaultLib = lib;
		this.defaultType = provider;
		this.defaultDisplayStyle = displayStyle;
	}

	/**
	 * If set to false, then the id of the currently selected marker is not transferred to 
	 * the server. 
	 * By default, this is true (except for global widgets, whose default is false)
	 * @param defaultTrackMarkers
	 */
	public void setDefaultTrackMarkers(boolean defaultTrackMarkers) {
		this.defaultTrackMarkers = defaultTrackMarkers;
	}
	
	/**
	 * Note: this will always return null if {@link #setDefaultTrackMarkers(boolean)} is
	 * set to false.
	 * @param req
	 * @return
	 * 		the currently selected marker, or null if no marker is selected or 
	 * 		{@link #setDefaultTrackMarkers(boolean)} is set to false.
	 */
	public Marker getSelectedMarker(OgemaHttpRequest req) {
		return getData(req).getSelectedMarker();
	}
	
	public void setInfoWindowWidget(OgemaWidget widget) {
		this.infoWindowWidget = (OgemaWidgetBase<?>) widget;
	}
	
	@Override
	public void destroyWidget() {
		super.destroyWidget();
		if (infoWindowWidget!=null)
			infoWindowWidget.destroyWidget();
	}
	
}
