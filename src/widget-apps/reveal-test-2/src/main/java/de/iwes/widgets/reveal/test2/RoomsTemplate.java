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
package de.iwes.widgets.reveal.test2;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ValueResource;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.reveal.base.ColumnTemplate;

@SuppressWarnings("serial")
class RoomsTemplate implements ColumnTemplate {

	private final AtomicInteger cnt = new AtomicInteger(0);
	private final ApplicationManager appMan;
	
	RoomsTemplate(final ApplicationManager appMan) {
		this.appMan = appMan;
	}
	
	static final class RoomSnippet extends PageSnippet {
		
		private final Header header;
		private final String room;
		private final Label roomTemp;
		private final Label roomHumidity;
		private final ValueInputField<Float> roomTempSetpoint;
		private final RedirectButton roomBaseRedirect;

		RoomSnippet(OgemaWidget widget, String id, OgemaHttpRequest req, Room room, ApplicationManager appMan) {
			super(widget, id, req);
			this.header = new Header(this, id + "_header", req);
			final NameService service = getNameService();
			String name = service == null ? null : service.getName(room, req.getLocale());
			if (name == null)
				name = ResourceUtils.getHumanReadableName(room);
//			this.room = name;
			this.room = ResourceUtils.getValidResourceName(room.getPath());
			header.setDefaultText("Room " + name);
			header.setDefaultHeaderType(2);
			this.roomTemp = new RoomSensorLabel<TemperatureSensor>(this, id + "_roomtemp", req, room, TemperatureSensor.class, appMan) {
				
				protected String format(double average) {
					return String.format(Locale.ENGLISH, "%.2f°C", average - 273.15F);
				}
				
			};
			this.roomHumidity = new RoomSensorLabel<HumiditySensor>(this, id + "_roomhumidity", req, room, HumiditySensor.class, appMan) {
				
				protected String format(double average) {
					return String.format(Locale.ENGLISH, "%.0f%%", 100 * average);
				}
				
			};
			this.roomTempSetpoint = new ValueInputField<Float>(this, id + "_roomTempSetpoint", Float.class, req) {
				
				public void onGET(OgemaHttpRequest req) {
					final OptionalDouble opt = appMan.getResourceAccess().getResources(Thermostat.class).stream()
						.filter(thermostat -> room.equalsLocation(ResourceUtils.getDeviceLocationRoom(thermostat)))
						.map(thermostat -> thermostat.temperatureSensor().deviceFeedback().setpoint())
						.filter(setpoint0 -> setpoint0.isActive())
						.mapToDouble(setpoint0 -> setpoint0.getCelsius())
						.average();
					if (!opt.isPresent()) {
						setNumericalValue(null, req);
						disable(req);
						setPollingInterval(-1, req);
					} else {
						setNumericalValue((float) opt.getAsDouble(), req);
						enable(req);
						setPollingInterval(120000, req);
					}
				}
				
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final Float value = getNumericalValue(req);
					if (value == null)
						return;
					appMan.getResourceAccess().getResources(Thermostat.class).stream()
						.filter(thermostat -> room.equalsLocation(ResourceUtils.getDeviceLocationRoom(thermostat)))
						.map(thermostat -> thermostat.temperatureSensor().settings().setpoint())
						.filter(setpoint -> setpoint.isActive())
						.forEach(setpoint -> setpoint.setCelsius(value));
				}
				
				
			};
			roomTempSetpoint.setDefaultPlaceholder("not available");
			roomTempSetpoint.setDefaultUnit("°C");
			roomTempSetpoint.triggerAction(roomTempSetpoint, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			this.roomBaseRedirect = new RedirectButton(this, id + "_baseRedirect", "Back to Rooms overview", "#rooms", req);
			roomBaseRedirect.setOpenInNewTab(false, req);
			buildPage(req);
			
		}
		
		private final void buildPage(final OgemaHttpRequest req) {
			this.append(header,req);
			final SimpleGrid grid = new SimpleGrid(this, getId() + "_grid", req)
					.addItem("Temperature:", false, req).addItem(roomTemp, false, req)
					.addItem("Humidity:", true, req).addItem(roomHumidity, false, req)
					.addItem("Heating setpoint: ", true, req).addItem(roomTempSetpoint, false, req)
					.addItem(roomBaseRedirect, true, req);
			grid.setAppendFillColumn(true, req);
			grid.setPrependFillColumn(true, req);
//			grid.setColumnTemplate("1fr auto auto 1fr", req);
			this.append(grid, req);
		}
		
		String getRoom() {
			return room;
		}
		
	}

	static class RoomSensorLabel<S extends Sensor> extends Label {

		private final Room room;
		private final Class<S> type;
		private final ApplicationManager appMan;
		
		RoomSensorLabel(OgemaWidget parent, String id, OgemaHttpRequest req, Room room, Class<S> type, ApplicationManager appMan) {
			super(parent, id, req);
			this.room = room;
			this.type = type;
			this.appMan = appMan; 
		}
		
		public void onGET(OgemaHttpRequest req) {
			final double temp = appMan.getResourceAccess().getResources(type).stream()
				.filter(sensor -> sensor.reading().isActive())
				.filter(sensor -> room.equalsLocation(ResourceUtils.getDeviceLocationRoom(sensor)))
				.mapToDouble(sensor -> ((Number) ValueResourceUtils.getValue((ValueResource) sensor.reading())).doubleValue())
				.average().orElse(Double.NaN);
			if (!Double.isNaN(temp))
				setPollingInterval(120000, req);
			else
				setPollingInterval(-1, req);
			setText(format(temp), req);
		}
		
		protected String format(double average) {
			return String.format(Locale.ENGLISH, "%.2f", average);
		}
		
		
	}
	
	@Override
	public Map<String, PageSnippetI> update(final OgemaWidget parent, final OgemaHttpRequest req, Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggered) {
		return appMan.getResourceAccess().getResources(Room.class).stream()
				.filter(Resource::isActive)
				.map(r -> new RoomSnippet(parent, ResourceUtils.getValidResourceName(r.getPath()) + "_" + cnt.getAndIncrement(), req, r, appMan))
				.collect(Collectors.toMap(RoomSnippet::getRoom, Function.identity()));
	}
};
