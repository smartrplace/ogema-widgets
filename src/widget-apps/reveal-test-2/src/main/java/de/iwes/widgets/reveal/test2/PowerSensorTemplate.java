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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.devices.sensoractordevices.MultiSwitchBox;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.reveal.base.ColumnTemplate;

@SuppressWarnings("serial")
class PowerSensorTemplate implements ColumnTemplate {

	private final AtomicInteger cnt = new AtomicInteger(0);
	private final ApplicationManager appMan;
	
	PowerSensorTemplate(final ApplicationManager appMan) {
		this.appMan = appMan;
	}
	
	static final class PowerSensorSnippet extends PageSnippet {
		
		private final Header header;
		private final String connectionId;
		private final Label room; // or building/-property unit
		private final Label power;
		private final Label voltage;
		private final Label current;
		private final Label subphases;
		private final RedirectButton sensorsBaseRedirect;

		PowerSensorSnippet(OgemaWidget widget, String id, OgemaHttpRequest req, ElectricityConnection connection, ApplicationManager appMan) {
			super(widget, id, req);
			this.header = new Header(this, id + "_header", req);
			final NameService service = getNameService();
			String name = service == null ? null : service.getName(connection, req.getLocale());
			if (name == null)
				name = ResourceUtils.getHumanReadableName(connection);
//			this.room = name;
			this.connectionId = ResourceUtils.getValidResourceName(connection.getPath());
			header.setDefaultText("Electric connection: " + name);
			header.setDefaultHeaderType(2);
			this.room = new Label(this, id + "_room", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					PhysicalElement target = null;
					try {
						target = ResourceUtils.getDeviceLocationRoom(connection);
					} catch (SecurityException e) {}
					if (target == null)
						target = ResourceUtils.getFirstContextResource(connection, Building.class);
					if (target == null)
						target = ResourceUtils.getFirstContextResource(connection, BuildingPropertyUnit.class);
					if (target != null)
						setText(ResourceUtils.getHumanReadableName(target), req);
					else
						setText("n.a.", req);
				}
				
			};
			this.power = new Label(this, id + "_power", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!connection.powerSensor().reading().isActive()) {
						setText("n.a.", req);
						setPollingInterval(-1, req);
					} else {
						setText(String.format(Locale.ENGLISH, "%.2f W",connection.powerSensor().reading().getValue()), req);
						setPollingInterval(15000, req);
					}
				}
				
			};
			this.voltage  = !connection.voltageSensor().isActive() ? null : new Label(this, id + "_voltage", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!connection.voltageSensor().reading().isActive()) {
						setText("n.a.", req);
						setPollingInterval(-1, req);
					} else {
						setText(String.format(Locale.ENGLISH, "%.1f V",connection.voltageSensor().reading().getValue()), req);
						setPollingInterval(15000, req);
					}
				}
				
			};
			this.current  = !connection.currentSensor().isActive() ? null : new Label(this, id + "_current", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!connection.currentSensor().reading().isActive()) {
						setText("n.a.", req);
						setPollingInterval(-1, req);
					} else {
						setText(String.format(Locale.ENGLISH, "%.2f A",connection.currentSensor().reading().getValue()), req);
						setPollingInterval(15000, req);
					}
				}
				
			};
			this.subphases = !connection.subPhaseConnections().isActive() ? null : new Label(this, id + "_subphases", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final StringBuilder html = new StringBuilder();
					html.append("<ul>");
					final AtomicInteger cnt = new AtomicInteger();
					connection.subPhaseConnections().getAllElements().stream()
						.sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
						.forEach(phase -> {
							html.append("<li>Phase ").append(cnt.incrementAndGet()).append(':').append(' ');
							if (phase.powerSensor().reading().isActive())
								html.append(String.format(Locale.ENGLISH, "%.2f W", phase.powerSensor().reading().getValue()));
							else
								html.append("n.a.");
							html.append("</li>");
						});
					html.append("</ul>");
					setHtml(html.toString(), req);
					setPollingInterval(30000, req);
				}
				
			};

			this.sensorsBaseRedirect = new RedirectButton(this, id + "_baseRedirect", "Back to power sensors overview", "#powerSensors", req);
			sensorsBaseRedirect.setOpenInNewTab(false, req);
			buildPage(req);
		}
		
		private final void buildPage(final OgemaHttpRequest req) {
			this.append(header,req);
			final SimpleGrid grid = new SimpleGrid(this, getId() + "_grid", req)
					.addItem("Room/Building: ", true, req).addItem(room, false, req)
					.addItem("Power: ", true, req).addItem(power, false, req);
			if (voltage != null)
				grid.addItem("Voltage: ", true, req).addItem(voltage, false, req);
			if (current != null)
				grid.addItem("Current: ", true, req).addItem(current, false, req);
			if (subphases != null)
				grid.addItem("Subphases: ", true, req).addItem(subphases, false, req);
			grid.addItem(sensorsBaseRedirect, true, req);
			grid.setAppendFillColumn(true, req);
			grid.setPrependFillColumn(true, req);
			grid.setRowGap("0.5em", req);
//			grid.setColumnTemplate("1fr auto auto 1fr", req);
			this.append(grid, req);
		}
		
		String getConnection() {
			return connectionId;
		}
		
	}

	@Override
	public Map<String, PageSnippetI> update(final OgemaWidget parent, final OgemaHttpRequest req, Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggered) {
		return getConnections(appMan)
				.map(r -> new PowerSensorSnippet(parent, ResourceUtils.getValidResourceName(r.getPath()) + "_" + cnt.getAndIncrement(), 
						req, r, appMan))
				.collect(Collectors.toMap(PowerSensorSnippet::getConnection, Function.identity()));
	}
	
	static Stream<ElectricityConnection> getConnections(final ApplicationManager appMan) {
		return appMan.getResourceAccess().getResources(ElectricityConnection.class).stream()
			.filter(elConn -> {
				final Resource parent = elConn.getParent();
				return !(parent instanceof ResourceList<?> && parent.getName().equals("subPhaseConnections"));
					
			})
			.filter(r -> !(r.getParent() instanceof SingleSwitchBox) && !(r.getParent() instanceof MultiSwitchBox))
			.filter(elconn -> elconn.powerSensor().isActive());
	}
	
};
