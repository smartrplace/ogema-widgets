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
import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.reveal.base.ColumnTemplate;

@SuppressWarnings("serial")
class LightsTemplate implements ColumnTemplate {

	private final AtomicInteger cnt = new AtomicInteger(0);
	private final ApplicationManager appMan;
	
	LightsTemplate(final ApplicationManager appMan) {
		this.appMan = appMan;
	}
	
	static final class LightsSnippet extends PageSnippet {
		
		private final Header header;
		private final String light;
		private final Label on;
		private final Label room;
		private final Button onOffToggle;
		private final RedirectButton lightsBaseRedirect;

		LightsSnippet(OgemaWidget widget, String id, OgemaHttpRequest req, ElectricLight light, ApplicationManager appMan,
				Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggereds) {
			super(widget, id, req);
			this.header = new Header(this, id + "_header", req);
			final NameService service = getNameService();
			String name = service == null ? null : service.getName(light, req.getLocale());
			if (name == null)
				name = ResourceUtils.getHumanReadableName(light);
//			this.room = name;
			this.light = ResourceUtils.getValidResourceName(light.getPath());
			header.setDefaultText("Light " + name);
			header.setDefaultHeaderType(2);
			this.on = new Label(this, id + "_on", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!light.onOffSwitch().stateFeedback().isActive()) {
						setText("n.a.", req);
						setPollingInterval(-1, req);
					} else {
						setText(light.onOffSwitch().stateFeedback().getValue() ? "on" : "off", req);
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
			// TODO a nicer toggle
			this.onOffToggle = new Button(this, id+ "_toggle", req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!light.onOffSwitch().stateFeedback().isActive() || !light.onOffSwitch().stateControl().exists()) {
						setText("Not available", req);
						disable(req);
						return;
					} 
					final boolean enabled = light.onOffSwitch().stateFeedback().getValue();
					setText(enabled ? "Off" : "On", req);
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					if (!light.onOffSwitch().stateFeedback().isActive() || !light.onOffSwitch().stateControl().exists()) {
						setText("Not available", req);
						disable(req);
						return;
					} 
					final boolean enabled = light.onOffSwitch().stateFeedback().getValue();
					light.onOffSwitch().stateControl().setValue(!enabled);
				}
				
			};
			this.lightsBaseRedirect = new RedirectButton(this, id + "_baseRedirect", "Back to lights overview", "#lights", req);
			lightsBaseRedirect.setOpenInNewTab(false, req);
			buildPage(req);
			setDependencies(req);
			if (triggers != null)
				triggers.forEach(trigger -> trigger(trigger, req));
			if (triggereds != null)
				triggereds.forEach(triggered -> triggered(triggered, req));
		}
		
		private final void buildPage(final OgemaHttpRequest req) {
			this.append(header,req);
			final SimpleGrid grid = new SimpleGrid(this, getId() + "_grid", req)
					.addItem("State: ", true, req).addItem(on, false, req)
					.addItem("Room: ", true, req).addItem(room, false, req)
					.addItem("Toggle: ", true,req).addItem(onOffToggle, false, req)
					.addItem(lightsBaseRedirect, true, req);
			grid.setAppendFillColumn(true, req);
			grid.setPrependFillColumn(true, req);
			grid.setRowGap("0.5em", req);
//			grid.setColumnTemplate("1fr auto auto 1fr", req);
			this.append(grid, req);
		}
		
		String getLight() {
			return light;
		}
		
		private final void trigger(final OgemaWidget trigger, final OgemaHttpRequest req) {
			trigger.triggerAction(on, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			trigger.triggerAction(onOffToggle, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
		}
		
		private final void triggered(final OgemaWidget triggered, final OgemaHttpRequest req) {
			onOffToggle.triggerAction(triggered, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
		}
		
		private final void setDependencies(final OgemaHttpRequest req) {
			this.onOffToggle.triggerAction(onOffToggle, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			this.onOffToggle.triggerAction(on, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
		}
		
	}

	@Override
	public Map<String, PageSnippetI> update(final OgemaWidget parent, final OgemaHttpRequest req, Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggered) {
		return appMan.getResourceAccess().getResources(ElectricLight.class).stream()
				.filter(Resource::isActive)
				.map(r -> new LightsSnippet(parent, ResourceUtils.getValidResourceName(r.getPath()) + "_" + cnt.getAndIncrement(), 
						req, r, appMan, triggers, triggered))
				.collect(Collectors.toMap(LightsSnippet::getLight, Function.identity()));
	}
};
