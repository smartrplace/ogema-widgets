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
package org.ogema.apps.roomlink;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.ogema.apps.roomlink.localisation.roomdetails.RoomDetailsDictionary;
import org.ogema.apps.roomlink.roomdetailspage.RoomContext;
import org.ogema.apps.roomlink.roomdetailspage.TemperatureRowTemplate;
import org.ogema.apps.roomlink.roomdetailspage.TemperatureSensorPattern;
import org.ogema.apps.roomlink.utils.RoomLinkUtils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.humread.valueconversion.SchedulePresentationData;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.resource.util.LoggingUtils;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.pattern.widget.table.ContextPatternTable;
import de.iwes.widgets.reswidget.scheduleplot.flot.ScheduleDataFlot;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;

public class RoomDetailsPageBuilder {
	
	static void addWidgets(final WidgetPage<RoomDetailsDictionary> page, ApplicationManager am,NameService nameService) {
		
		// TODO remove, just for testing
		Header currentRoomLabel  =new Header(page, "currentRoomLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {				
				String[] params = page.getPageParameters(req).get("configId");
				String text = "Room not specified";
				if (params != null && params.length > 0) {
					text = params[0];
				}
				setText(text, req);
			}
		};
		currentRoomLabel.addDefaultStyle(HeaderData.CENTERED);
		page.append(currentRoomLabel).linebreak();
		
		Accordion dataAccordion = new Accordion(page, "dataAccordion", true);
		addAccordionItems(dataAccordion, page, am, nameService);
		
		page.append(dataAccordion);
		
	}
	
	private static void addAccordionItems(Accordion accordion, final WidgetPage<RoomDetailsDictionary> page, final ApplicationManager am,NameService nameService) {
		PageSnippet temperatureSnippet = addTemperatureItems(accordion, page, am, nameService);
		
		accordion.addItem("temperature", temperatureSnippet, null);
	}
	
	private static PageSnippet addTemperatureItems(Accordion accordion, final WidgetPage<RoomDetailsDictionary> page, final ApplicationManager am, NameService nameService) {
		PageSnippet temperatureSnippet = new PageSnippet(page, "temperatureSnippet", true);
		final Checkbox loggingEnabled = new Checkbox(page, "loggingEnable") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				Room room = RoomLinkUtils.getActiveResource(page, req, am.getResourceAccess());
				boolean value = false;
				if (room == null || !room.temperatureSensor().isActive()) {
					disable(req); // working?
				}
				else {
					enable(req);
					value = LoggingUtils.isLoggingEnabled(room.temperatureSensor().reading());
				}
				Map<String,Boolean> loggingCheckboxOptions  =new HashMap<String, Boolean>();
				loggingCheckboxOptions.put(page.getDictionary(req.getLocaleString()).loggingEnabled(), value);
				setCheckboxList(loggingCheckboxOptions,req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				Room room = RoomLinkUtils.getActiveResource(page, req, am.getResourceAccess());
				if (room == null || !room.isActive() || !room.temperatureSensor().isActive()) return; 
				JSONObject obj = new JSONObject(data);
				if (!obj.has("data")) return; // should not happen
				String dat = obj.getString("data");
				try {
		            String[] map = dat.split("&");
		            String entry = map[0];
//		                String key = entry.split("=")[0];
	                String value = entry.split("=")[1];
	                boolean loggingEnabled = Boolean.valueOf(value);
	                if (loggingEnabled) {
	                	long updateInterval = 60000; // default value
	                	LoggingUtils.activateLogging(room.temperatureSensor().reading(), updateInterval);
	                }
	                else {
	                	LoggingUtils.deactivateLogging(room.temperatureSensor().reading());
	                }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		};
		final SchedulePlotFlot temperaturePlot = new SchedulePlotFlot(page, "temperaturePlot", false) {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				ScheduleDataFlot data = getScheduleData(req);
				Room room = RoomLinkUtils.getActiveResource(page, req, am.getResourceAccess());
				if (room == null || !room.isActive()) {
					Map<String,SchedulePresentationData> empty = Collections.emptyMap();
					data.setSchedules(empty); 
					return;
				}
				RoomContext rc = new RoomContext();
				rc.room = room;
				List<TemperatureSensorPattern> tempSensors = am.getResourcePatternAccess().getPatterns(TemperatureSensorPattern.class, AccessPriority.PRIO_LOWEST, rc);
				Map<String,SchedulePresentationData> schedules = new LinkedHashMap<String, SchedulePresentationData>();
				TemperatureSensor roomSensor = room.temperatureSensor();
				boolean mainSensorContained = false;
				for (TemperatureSensorPattern pattern : tempSensors) {
					RoomDetailsPageBuilder.addSensor(schedules, pattern.model);
//					if (pattern.model.equalsLocation(roomSensor)) mainSensorContained = true; // FIXME it is not clear which of the two is logging... 
				}
				if (!mainSensorContained) {
					RoomDetailsPageBuilder.addSensor(schedules, roomSensor);
				}
				data.setSchedules(schedules);
				Boolean loggingActive = loggingEnabled.getCheckboxList(req).get(page.getDictionary(req.getLocaleString()).loggingEnabled());
				if (loggingActive == null) loggingActive = false;
				if (loggingActive) 
					setPollingInterval(5000, req);
				else
					setPollingInterval(-1, req);
			}
		};
		
		temperatureSnippet.append(temperaturePlot, null).linebreak(null);
		
/*		Label loggingEnabledLabel = new Label(page, "loggingEnabledLabel") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).loggingEnabled(), req);
				Room room = config.getActiveResource(req);
				if (!room.temperatureSensor().isActive()) 
					disable(req); // working?
				else 
					enable(req); // working?
			}
		}; */
		


		Label tempLoggingIntervalLabel = new Label(page, "tempLoggingIntervalLabel") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).loggingInterval(), req);
			}
		};
		
		TextField logUpdateTime = new TextField(page, "logUpdateTime") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Room room = RoomLinkUtils.getActiveResource(page, req, am.getResourceAccess());
				if (room == null || !room.isActive() || !room.temperatureSensor().isActive() 
							|| !LoggingUtils.isLoggingEnabled(room.temperatureSensor().reading())) {
					disable(req);
					setValue("", req);
					return;
				}
				enable(req);
				RecordedData rd = room.temperatureSensor().reading().getHistoricalData();
				long updateTime  = rd.getConfiguration().getFixedInterval();
				setValue(String.valueOf(updateTime/1000) + " s", req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				Room room = RoomLinkUtils.getActiveResource(page, req, am.getResourceAccess());
				if (room == null || !room.isActive() || !room.temperatureSensor().isActive() 
						|| !LoggingUtils.isLoggingEnabled(room.temperatureSensor().reading())) {
					return;
				}
				String value = getValue(req);
				if (value == null) return;
				if (value.toLowerCase().endsWith("s")) {  // remove a potential seconds identifier string
					value = value.substring(0, value.length() -1 ).trim();
				}
				long seconds;
				try {
					seconds = Long.parseLong(value);
				} catch (NumberFormatException e) {
					// TODO set alert value
					e.printStackTrace();
					return;
				}
				if (seconds <= 0) {
					// TODO set alert value
					return;
				}
				LoggingUtils.activateLogging(room.temperatureSensor().reading(), seconds * 1000);
				
			}
		};
		
		StaticTable tempStatTable  = new StaticTable(1, 3, new int[]{2,2,2});
		tempStatTable.setContent(0, 0, loggingEnabled).setContent(0, 1, tempLoggingIntervalLabel).setContent(0, 2, logUpdateTime);
		temperatureSnippet.append(tempStatTable, null).linebreak(null);
//		temperatureSnippet.append(loggingEnabled, null).append(tempLoggingIntervalLabel, null).append(logUpdateTime, null);
		loggingEnabled.triggerAction(logUpdateTime, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingEnabled.triggerAction(temperaturePlot, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		logUpdateTime.triggerAction(logUpdateTime, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		Label measuredTempValues = new Label(page, "measuredTempValues") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).measuredTemperaturesLabel(), req);
			}
			
		};
		temperatureSnippet.append(measuredTempValues, null).linebreak(null);
		
		TemperatureRowTemplate template = new TemperatureRowTemplate(page, nameService);
		ContextPatternTable<TemperatureSensorPattern, RoomContext> temperatureValuesTable
		 	= new ContextPatternTable<TemperatureSensorPattern, RoomContext>(page,"temperatureValuesTable", false, 
		 			TemperatureSensorPattern.class, template, am.getResourcePatternAccess()) {
		
			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				Room room = RoomLinkUtils.getActiveResource(page, req, am.getResourceAccess());
				RoomContext ctx = new RoomContext();
				ctx.room = room;
				setContext(ctx, req);
			};
		};
		
//		temperatureValuesTable.setHeader(template.getHeader(null, null));
//		temperatureValuesTable.setRowTemplate(template);
		Set<WidgetStyle<?>> styles = new LinkedHashSet<WidgetStyle<?>>();
		styles.add(DynamicTableData.CELL_ALIGNMENT_LEFT);
		temperatureValuesTable.setDefaultStyles(styles);
		temperatureSnippet.append(temperatureValuesTable, null);

		return temperatureSnippet;
	}
	
	private static void addSensor(Map<String, SchedulePresentationData> schedules, Sensor sensor) {
		ValueResource vr  =sensor.reading();
		if (!(vr instanceof SingleValueResource)) return;
		SingleValueResource svr  = (SingleValueResource) vr;
		if (sensor.isActive() && LoggingUtils.isLoggingEnabled(svr)) {
			AbsoluteSchedule historicalData  = LoggingUtils.getHistoricalDataSchedule(svr).<AbsoluteSchedule> create();
			historicalData.activate(false);
			schedules.put(historicalData.getPath(), new DefaultSchedulePresentationData(historicalData, vr.getResourceType(), historicalData.getPath()));
		}
	}
	
}
 