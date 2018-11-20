/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.widgets.test3.template.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.geomap.MapLib;
import de.iwes.widgets.html.geomap.MapProvider;
import de.iwes.widgets.html.geomap.MapProvider.DisplayStyle;
import de.iwes.widgets.html.geomap.Marker;
import de.iwes.widgets.html.geomap.TemplateMap;
import de.iwes.widgets.test3.impl.WidgetTest;

public class TemplatePageBuilder {
	
	private final WidgetPage<?> page;
	private final Header header;
	private final EnumDropdown<MapLib> mapLibSelector;
	private final EnumDropdown<MapProvider> mapTypeSelector;
	private final TemplateDropdown<DisplayStyle> mapDisplayStyleSelector;
	private final TemplateMap<MarkerType> map;
	private final WidgetGroup markerInfoWidgets;
	
	public TemplatePageBuilder(final WidgetPage<?> page, final String googleApiKey, final String bingApiKey, final Logger logger) {
		this.page = page;
		this.header = new Header(page, "header", true);
		header.setDefaultText("Map test page");
		header.addDefaultStyle(HeaderData.CENTERED);
		header.setDefaultColor("blue");
		
		this.mapLibSelector = new EnumDropdown<MapLib>(page, "mapLibSelector", MapLib.class) {

			private static final long serialVersionUID = 1L;

			@Override
			protected MapLib[] getAllElements() {
				if (googleApiKey != null)
					return super.getAllElements();
				else {
					MapLib[] all = super.getAllElements();
					MapLib[] arr = new MapLib[all.length-1];
					int cnt = 0;
					for (MapLib lib :all) {
						if (lib == MapLib.GOOGLE) 
							continue;
						arr[cnt++] = lib;
					}
					return arr;
				}
					
			}
			
		};
		this.mapTypeSelector = new EnumDropdown<MapProvider>(page, "mapTypeSelector", MapProvider.class) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final boolean googleKeyAvailable = googleApiKey != null;
				final boolean bingKeyAvailable = bingApiKey != null;
				final List<MapProvider> providers = new ArrayList<>();
				final MapLib lib= mapLibSelector.getSelectedItem(req);
				for (MapProvider provider :lib.getSupportedMaps()) {
					if ((!googleKeyAvailable && provider == MapProvider.GOOGLE) || (!bingKeyAvailable && provider == MapProvider.BING)) 
						continue;
					providers.add(provider);
				}
				update(providers, req);
			}
			
		};
		mapLibSelector.selectDefaultItem(MapLib.OPENLAYERS);
		mapTypeSelector.selectDefaultItem(MapProvider.OPENSTREETMAP);
		this.mapDisplayStyleSelector = new TemplateDropdown<DisplayStyle>(page, "mapDisplayStyleSelector") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final MapProvider type = mapTypeSelector.getSelectedItem(req);
				if (type == null) {
					update(Collections.<DisplayStyle> emptyList(), req);
					return;
				}
				update(type.getSupportedDisplayTypes(), req);
			}
			
		};
		mapDisplayStyleSelector.setTemplate(MapProvider.DISPLAY_STYLE_TEMPLATE);
//		this.markerInfoSnippet = new PageSnippet(page, "markerInfoSnippet", true);
		this.markerInfoWidgets = page.registerWidgetGroup("markerInfoWidgets");
		this.map = new TemplateMap<MarkerType>(page, "map", new MarkerTemplate()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setType(mapLibSelector.getSelectedItem(req), mapTypeSelector.getSelectedItem(req), 
						mapDisplayStyleSelector.getSelectedItem(req), req);			}
			
		};
		if (googleApiKey != null)
			map.setApiKey(MapProvider.GOOGLE, googleApiKey);
		if (bingApiKey != null)
			map.setApiKey(MapProvider.BING, bingApiKey);
	
		
		final MarkerType systec = new MarkerType("systec", 51.387455, 9.539532, "SysTec", WidgetTest.ICONS_BASE + "/sun1.svg");
		final MarkerType sma = new MarkerType("sma", 51.313051, 9.538593, "SMA", WidgetTest.ICONS_BASE + "/sun1.svg");
		final MarkerType soehre = new MarkerType("soehre", 51.245812, 9.52374, "Soehre", WidgetTest.ICONS_BASE + "/wind1.svg");
		final MarkerType liebenau = new MarkerType("liebenau", 51.506373, 9.252554, "Liebenau", WidgetTest.ICONS_BASE + "/wind1.svg");
		
		final PageSnippet infoWindowSnippet = new PageSnippet(page, "infoWindowSnippet", true);
		final Label infoLabel = new Label(page, "infoLabel") {
			
			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				final Marker marker = map.getSelectedMarker(req);
				if (marker ==  null)
					setText("", req);
				else
					setText(marker.getId(), req);
			};
			
		};
		infoWindowSnippet.append(infoLabel, null);
		map.setInfoWindowWidget(infoWindowSnippet);
		
		map.setDefaultItems(Arrays.asList(systec,sma,soehre,liebenau));
		map.setDefaultZoom((short) 10);
		map.setDefaultCenter(new double[]{51.313645, 9.447138});
		map.triggerAction(infoLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		setInfoWidgets();
		buildPage();
		setDependencies();
	}
	
//	private final void setInfoWidgets() {
//		final Label label = new Label(page, "label") {
//			
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void onGET(OgemaHttpRequest req) {
//				final MarkerType m = map.getSelectedItem(req);
//				setText(m != null ? m.title : "", req);
//			}
//			
//		};
//		markerInfoSnippet.append(label, null);
//		markerInfoWidgets.addWidget(label);
//	}

	private void buildPage() {
		int row =0;
		final StaticTable tab = new StaticTable(3, 3, new int[]{2,2,8})
				.setContent(row, 0, "Select map library").setContent(row++, 1, mapLibSelector)
				.setContent(row, 0, "Select map provider").setContent(row++, 1, mapTypeSelector)
				.setContent(row, 0, "Select map	style").setContent(row++, 1, mapDisplayStyleSelector);
		page.append(tab);
		page.append(header).append(tab).linebreak().append(map);
	}
	
	private void setDependencies() {
		mapLibSelector.triggerAction(mapTypeSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		mapLibSelector.triggerAction(mapDisplayStyleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
		mapLibSelector.triggerAction(map, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,2);

		mapTypeSelector.triggerAction(mapDisplayStyleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		mapTypeSelector.triggerAction(map, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
		map.triggerAction(markerInfoWidgets, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		mapDisplayStyleSelector.triggerAction(map, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

	}
}
