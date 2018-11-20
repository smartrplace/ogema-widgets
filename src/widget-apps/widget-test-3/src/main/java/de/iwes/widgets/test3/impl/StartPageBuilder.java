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
package de.iwes.widgets.test3.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.geomap.GeoMap;
import de.iwes.widgets.html.geomap.MapLib;
import de.iwes.widgets.html.geomap.MapProvider;
import de.iwes.widgets.html.geomap.MapProvider.DisplayStyle;
import de.iwes.widgets.html.geomap.Marker;

public class StartPageBuilder {
	
	private final WidgetPage<?> page;
	private final Header header;
	private final EnumDropdown<MapLib> mapLibSelector;
	private final EnumDropdown<MapProvider> mapTypeSelector;
	private final TemplateDropdown<DisplayStyle> mapDisplayStyleSelector;
	private final GeoMap map;
	
	public StartPageBuilder(final WidgetPage<?> page, final String googleApiKey, final String bingApiKey, final Logger logger) {
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
		this.map = new GeoMap(page, "map") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setType(mapLibSelector.getSelectedItem(req), mapTypeSelector.getSelectedItem(req), 
							mapDisplayStyleSelector.getSelectedItem(req), req);
			}
			
		};
		if (googleApiKey != null)
			map.setApiKey(MapProvider.GOOGLE, googleApiKey);
		if (bingApiKey != null)
			map.setApiKey(MapProvider.BING, bingApiKey);
		
		final Marker systec = new Marker("systec", new double[]{51.387455, 9.539532});
		final int[] size = new int[]{50,50};
		systec.setIcon(WidgetTest.ICONS_BASE + "/sun1.svg", size);
		systec.setTitle("SysTec");
		systec.setInfoWindowHtml("<h3>SysTec</h3><br>Forschungslabor für Netzintegration");
		
		final Marker sma = new Marker("sma", new double[]{51.313051, 9.538593});
		sma.setIcon(WidgetTest.ICONS_BASE + "/sun1.svg", size);
		sma.setInfoWindowHtml("<h3>SMA</h3><br>Hersteller von PV-Wechselrichtern");
		
		final Marker soehre = new Marker("soehre", new double[]{51.245812, 9.523745});
		soehre.setIcon(WidgetTest.ICONS_BASE + "/wind1.svg", size);
		soehre.setTitle("Windpark S&#246;hre"); // &#246;=ö in html 
		soehre.setInfoWindowHtml("<h3>Windpark S&#246;hre</h3><br><a target=\"_blank\" href=\"http://www.windpark-soehrewald-niestetal.de/home/\">Weitere Informationen</a>");
		
		final Marker liebenau = new Marker("liebenau", new double[]{51.506373, 9.252554});
		liebenau.setIcon(WidgetTest.ICONS_BASE + "/wind1.svg", size);
		liebenau.setTitle("Windpark Liebenau");
		liebenau.setInfoWindowHtml("<h3>Windpark Liebenau</h3><br><a target=\"_blank\" href=\"http://www.proplanta.de/Maps/Windpark+Liebenau_poi1408449028.html\">Weitere Informationen</a>");
//		http://www.proplanta.de/Maps/Windpark+Liebenau+34396+Liebenau_poi_standort1408449028.html
		
		map.setDefaultMarkers(Arrays.asList(systec,sma,soehre,liebenau));
		map.setDefaultZoom((short) 10); 
		buildPage();
		setDependencies();
	}

	private void buildPage() {
		int row =0;
		final StaticTable tab = new StaticTable(3, 3, new int[]{2,2,8})
				.setContent(row, 0, "Select map library").setContent(row++, 1, mapLibSelector)
				.setContent(row, 0, "Select map provider").setContent(row++, 1, mapTypeSelector)
				.setContent(row, 0, "Select map	style").setContent(row++, 1, mapDisplayStyleSelector);
		
		page.append(header).append(tab).linebreak().append(map);
	}
	
	private void setDependencies() {
		mapLibSelector.triggerAction(mapTypeSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		mapLibSelector.triggerAction(mapDisplayStyleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
		mapLibSelector.triggerAction(map, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,2);

		mapTypeSelector.triggerAction(mapDisplayStyleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		mapTypeSelector.triggerAction(map, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);

		mapDisplayStyleSelector.triggerAction(map, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
}
