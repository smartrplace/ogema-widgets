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
package org.ogema.widgets.trigger.level.test.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.SelectionTree;
import de.iwes.widgets.html.selectiontree.samples.BaseDeviceTypeOption;
import de.iwes.widgets.html.selectiontree.samples.RoomTypeOption;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;
import de.iwes.widgets.html.selectiontree.samples.resource.DeviceOptionResource;
import de.iwes.widgets.html.selectiontree.samples.resource.ResourceLeaf;
import de.iwes.widgets.html.selectiontree.samples.resource.ResourceLeaf.ResourceLeafSelectionItem;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;
import de.iwes.widgets.resource.timeseries.TimeSeriesDataType;
import de.iwes.widgets.html.selectiontree.samples.resource.ResourceTimeSeriesOption;
import de.iwes.widgets.html.selectiontree.samples.resource.RoomOptionResource;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class SelectionTreePage {
	
	public SelectionTreePage(final WidgetPage<?> page, final ApplicationManager am, final NameService nameService, final OnlineTimeSeriesCache cache) {
		
		final Header header = new Header(page, "header");
		header.setDefaultText("This is a widget test page");
		header.addDefaultStyle(HeaderData.CENTERED);
		page.append(header).linebreak();
		
		final LinkingOption o1 = new LinkingOption(true,false) {
			
			private final List<SelectionItem> items = Collections.unmodifiableList(Arrays.<SelectionItem> asList(
					new SelectionItemImpl("afrique", "Africa"),
					new SelectionItemImpl("leurope", "Europe"),
					new SelectionItemImpl("aust", "Australia")
			));

			@Override
			public String id() {
				return "continent";
			}

			@Override
			public String label(OgemaLocale locale) {
				return "Select a continent";
			}

			@Override
			public LinkingOption[] dependencies() {
				return null;
			}

			@Override
			public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
				return items;
			}
			
		};
		
		final LinkingOption o2 = new LinkingOption() {
			
			private final SelectionItem botswana = new SelectionItemImpl("Botswana", "Botswana");
			private final SelectionItem norway = new SelectionItemImpl("Norway", "Norway");
			private final SelectionItem portugal = new SelectionItemImpl("Portugal", "Portugal");
			private final SelectionItem australia = new  SelectionItemImpl("Australia", "Australia");
			
			private final List<SelectionItem> items = Collections.unmodifiableList(Arrays.<SelectionItem> asList(
					botswana, norway, portugal, australia
			));
			
			@Override
			public String label(OgemaLocale locale) {
				return "Select a country";
			}
			
			@Override
			public String id() {
				return "country";
			}
			
			@Override
			public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
				if (dependencies == null || dependencies.isEmpty())
					return items;
				boolean a = false;
				boolean e = false;
				boolean aus = false;
				for (SelectionItem d : dependencies.iterator().next()) {
					switch (d.id()) {
					case "afrique":
						a = true;
						break;
					case "leurope":
						e = true;
						break;
					case "aust":
						aus = true;
						break;
					}
				}
				final List<SelectionItem> list = new ArrayList<>();
				if (a)
					list.add(botswana);
				if (e) {
					list.add(norway);
					list.add(portugal);
				}
				if (aus)
					list.add(australia);
				return list;
			}
			
			@Override
			public LinkingOption[] dependencies() {
				return new LinkingOption[]{o1};
			}
			
		};
		final LinkingOption o3 = new LinkingOption() {
			
			private final List<SelectionItem> items1 = Collections.unmodifiableList(Arrays.<SelectionItem> asList(
					new SelectionItemImpl("lion", "Lion"),
					new SelectionItemImpl("elph", "Elephant")
			));
			
			private final List<SelectionItem> items2 = Collections.unmodifiableList(Arrays.<SelectionItem> asList(
					new SelectionItemImpl("reindeer", "Reindeer"),
					new SelectionItemImpl("orca", "Orca")
			));
			
			@Override
			public String label(OgemaLocale locale) {
				return "Select an animal";
			}
			
			@Override
			public String id() {
				return "animeaux";
			}
			
			@Override
			public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
				boolean b = false;
				boolean n = false;
				boolean a = false;
				boolean p = false;
				if (dependencies == null || dependencies.isEmpty()) {
					b =  true;
					n = true;
					a = true;
					p = true;
				} else {
					Collection<SelectionItem> deps = dependencies.iterator().next();
					for (SelectionItem si : deps) {
						switch (si.id()) {
						case "Botswana":
							b = true;
							break;
						case "Norway":
							n = true;
							break;
						case "Australia":
							a = true;
							break;
						case "Portugal":
							p = true;
							break;
						}
					}
				}
				if (!b && !n && !a && !p)
					return Collections.emptyList();
				final List<SelectionItem> result = new ArrayList<>();
				if (b)
					result.addAll(items1);
				if (n)
					result.addAll(items2);
				if (a)
					result.add(new SelectionItemImpl("kang", "Kangaroo"));
				if (p && !n)
					result.add(new SelectionItemImpl("orca", "Orca"));
				return result;
			}
			
			@Override
			public LinkingOption[] dependencies() {
				return new LinkingOption[]{o2};
			}
		};
		final List<LinkingOption> options = Arrays.asList(o1,o2,o3);
		final SelectionTree tree = new SelectionTree(page, "selectionTree", true);
		tree.setDefaultBackgroundColor("DarkBlue"); 
		tree.setSelectionOptions(options, null);
		page.append(tree).linebreak();
		
		
		final RoomTypeOption roomTypes = new RoomTypeOption();
		final RoomOptionResource rooms = new RoomOptionResource(roomTypes, am.getResourceAccess());
		final BaseDeviceTypeOption deviceTypes = new BaseDeviceTypeOption();
		final DeviceOptionResource devices = new DeviceOptionResource(deviceTypes, rooms, am.getResourceAccess());
		final ResourceTimeSeriesOption timeSeries = new ResourceTimeSeriesOption(devices, am.getResourceAccess());
		final ResourceLeaf resourceLeaf = new ResourceLeaf(rooms, devices, timeSeries, am.getResourceAccess(), 
				nameService, cache);
		
		final SelectionTree resourceTree = new SelectionTree(page, "resourceSelectionTree", true);
		resourceTree.setDefaultBackgroundColor("DarkBlue"); 
		resourceTree.setSelectionOptions(Arrays.asList(roomTypes, rooms, deviceTypes, devices, timeSeries, resourceLeaf), null);
		page.append(resourceTree).linebreak();
		
		final SchedulePlotFlot schedulePlot = new SchedulePlotFlot(page, "schedulePlot", false) {
			
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void onGET(OgemaHttpRequest req) {
				final OgemaWidget w = resourceTree.getTerminalSelectWidget(req);
				if (!(w instanceof TemplateMultiselect)) {
					getScheduleData(req).setSchedules(Collections.<String,SchedulePresentationData> emptyMap());
					return;
				}
				final TemplateMultiselect<SelectionItem> multiselect = (TemplateMultiselect<SelectionItem>) w;
				final List<SelectionItem> items = multiselect.getSelectedItems(req);
				final Map<String,SchedulePresentationData> schedules = new LinkedHashMap<>();
				boolean containsOnlineData = false;
				for (SelectionItem item: items) {
					if (!(item instanceof ResourceLeafSelectionItem)) {
						am.getLogger().error("Invalid item type, not a resource selection item: " + item);
						continue;
					}
					final ResourceLeafSelectionItem rsi = (ResourceLeafSelectionItem) item;
					if (rsi.getDataType() == TimeSeriesDataType.ONLINE_DATA) {
//						continue;
						containsOnlineData = true;
					}
					final ReadOnlyTimeSeries t = rsi.getTimeSeries();
					schedules.put(rsi.id(), 
							new DefaultSchedulePresentationData(t, rsi.getSingleValueResource(), rsi.label(OgemaLocale.ENGLISH)));
				}
				getScheduleData(req).setSchedules(schedules);
				setPollingInterval(containsOnlineData ? 10000 : -1, req);
			}
			
		};
		page.append(schedulePlot);
		// GET is triggered by posts of other select widgets
		resourceTree.getTerminalSelectWidget(null).triggerAction(schedulePlot, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		resourceTree.getTerminalSelectWidget(null).triggerAction(resourceTree.getTerminalSelectWidget(null), 
				TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
	}
	
}
