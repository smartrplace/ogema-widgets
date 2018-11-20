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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.DoorWindowSensor;
import org.ogema.tools.resource.util.ResourceUtils;

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
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.reveal.base.ColumnTemplate;

@SuppressWarnings("serial")
class WindowsTemplate implements ColumnTemplate {

	private final AtomicInteger cnt = new AtomicInteger(0);
	private final ApplicationManager appMan;
	
	WindowsTemplate(final ApplicationManager appMan) {
		this.appMan = appMan;
	}
	
	static final class WindowsSnippet extends PageSnippet {
		
		private final Header header;
		private final String sensor;
		private final Label open;
		private final Label room;
		private final RedirectButton sensorsBaseRedirect;

		WindowsSnippet(OgemaWidget widget, String id, OgemaHttpRequest req, DoorWindowSensor light, ApplicationManager appMan,
				Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggereds) {
			super(widget, id, req);
			this.header = new Header(this, id + "_header", req);
			final NameService service = getNameService();
			String name = service == null ? null : service.getName(light, req.getLocale());
			if (name == null)
				name = ResourceUtils.getHumanReadableName(light);
//			this.room = name;
			this.sensor = ResourceUtils.getValidResourceName(light.getPath());
			header.setDefaultText("Sensor " + name);
			header.setDefaultHeaderType(2);
			this.open = new Label(this, id + "_on", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!light.reading().isActive()) {
						setText("n.a.", req);
						setPollingInterval(-1, req);
					} else {
						setText(light.reading().getValue() ? "open" : "closed", req);
						setPollingInterval(15000, req);
					}
				}
				
			};
			this.room = new Label(this, id + "_room", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					PhysicalElement target = null;
					try {
						target = ResourceUtils.getDeviceLocationRoom(light);
					} catch (SecurityException e) {}
					if (target != null)
						setText(ResourceUtils.getHumanReadableName(target), req);
					else
						setText("n.a.", req);
				}
				
			};
			this.sensorsBaseRedirect = new RedirectButton(this, id + "_baseRedirect", "Back to window sensors overview", "#windows", req);
			sensorsBaseRedirect.setOpenInNewTab(false, req);
			buildPage(req);
			if (triggers != null)
				triggers.forEach(trigger -> trigger(trigger, req));
		}
		
		private final void buildPage(final OgemaHttpRequest req) {
			this.append(header,req);
			final SimpleGrid grid = new SimpleGrid(this, getId() + "_grid", req)
					.addItem("State: ", true, req).addItem(open, false, req)
					.addItem("Room: ", true, req).addItem(room, false, req)
					.addItem(sensorsBaseRedirect, true, req);
			grid.setAppendFillColumn(true, req);
			grid.setPrependFillColumn(true, req);
			grid.setRowGap("0.5em", req);
//			grid.setColumnTemplate("1fr auto auto 1fr", req);
			this.append(grid, req);
		}
		
		String getLight() {
			return sensor;
		}
		
		private final void trigger(final OgemaWidget trigger, final OgemaHttpRequest req) {
			trigger.triggerAction(open, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
		}
		
	}

	@Override
	public Map<String, PageSnippetI> update(final OgemaWidget parent, final OgemaHttpRequest req, Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggered) {
		return appMan.getResourceAccess().getResources(DoorWindowSensor.class).stream()
				.filter(Resource::isActive)
				.map(r -> new WindowsSnippet(parent, ResourceUtils.getValidResourceName(r.getPath()) + "_" + cnt.getAndIncrement(), 
						req, r, appMan, triggers, triggered))
				.collect(Collectors.toMap(WindowsSnippet::getLight, Function.identity()));
	}
};
